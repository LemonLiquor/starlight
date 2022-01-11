package zhu.moon.guiwu

import net.mamoe.mirai.contact.PermissionDeniedException
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import zhu.moon.ExpendFeature
import zhu.moon.Plugin

/**
 * 用于撤回群里桂物发言
 */
object Guiwu : ExpendFeature() {
    private val bot = Plugin.instanceBot

    override fun main(){
        bot.eventChannel.subscribeAlways<GroupMessageEvent> {
            if (sender.id != 1837099861L ) return@subscribeAlways
            val msg = message.contentToString()

            /**
             * 当桂物消息中带回车，即超过一行，一般就是转发的烂活
             */
            if (msg.contains("\n")) {
                try {
                    message.recall()
                }catch ( p : PermissionDeniedException){
                    group.sendMessage("没有撤回权限，请群主设置")
                }catch (i:IllegalStateException ){
                    group.sendMessage("这句话已经被撤回了，我就不管了")
                }finally {
                    group.sendMessage("$senderName 的桂物发言已撤回")
                }
            }

        }
    }

}