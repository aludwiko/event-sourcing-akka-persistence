package info.ludwikowski.es.user

import akka.actor.{ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import com.softwaremill.tagging.Tagger
import com.typesafe.config.Config
import info.ludwikowski.es.user.application.{UserActor, UserActorTag, UserMessageExtractor}
import info.ludwikowski.es.user.application.UserActor.UserRegion

trait UserModule {

  def config: Config
  def system: ActorSystem

  lazy val userMessageExtractor: UserMessageExtractor = new UserMessageExtractor

  lazy val userRegion: UserRegion =
    ClusterSharding(system)
      .start(
        typeName = UserActor.Name,
        entityProps = Props(new UserActor),
        settings = ClusterShardingSettings(system),
        messageExtractor = userMessageExtractor
      )
      .taggedWith[UserActorTag]
}
