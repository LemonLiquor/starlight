package zhu.moon

import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.info
import zhu.moon.guiwu.Guiwu
import zhu.moon.reply.Reply
import java.io.File

object Plugin : KotlinPlugin(
    JvmPluginDescription(
        id = "zhu.moon.plugin",
        version = "0.1.2",
    )
) {
    //非空属性必须在定义的时候初始化,kotlin提供了一种可以延迟初始化的方案,使用 lateinit 关键字描述属性
    lateinit var instanceBot: Bot
    lateinit var admin: Contact

    //管理员QQ号，单独拎出来便于修改
    private val adminQQ = 1837099861L

    override fun onEnable() {
        logger.info { "Plugin loaded" }
        //配置文件目录 "${dataFolder.absolutePath}/"



        //机器人一旦上线就给机器人实例 bot 和管理员 admin 赋值
        globalEventChannel().subscribeOnce<BotOnlineEvent>{
            instanceBot = this.bot
            //注意long型尾缀
            admin = bot.getFriend(adminQQ)!!

            Guiwu.start()
            Reply.start()
        }
        
        globalEventChannel().subscribeAlways<GroupMessageEvent>{
            //群消息
            //复读示例
            if (message.contentToString().startsWith("复读")) {
                group.sendMessage(message.contentToString().replace("复读", ""))
            }

            if (message.contentToString() == "hi") {
                //群内发送
                group.sendMessage("hi")
                //向发送者私聊发送消息
                sender.sendMessage("hi")
                //不继续处理
                return@subscribeAlways
            }
            //分类示例
//            message.forEach {
//                //循环每个元素在消息里
//                if (it is Image) {
//                    //如果消息这一部分是图片
//                    val url = it.queryUrl()
//                    group.sendMessage("图片，下载地址$url")
//                }
//                if (it is PlainText) {
//                    //如果消息这一部分是纯文本
//                    group.sendMessage("纯文本，内容:${it.content}")
//                }
//            }
        }


        globalEventChannel().subscribeAlways<FriendMessageEvent>{
            sender.sendMessage("hi")
        }

       globalEventChannel().subscribeAlways<NewFriendRequestEvent> {
           admin.sendMessage("$fromNick: $fromId 在尝试添加机器人")
       }
       globalEventChannel().subscribeAlways<BotInvitedJoinGroupRequestEvent>{
           admin.sendMessage("$invitorNick: $invitorId 在拉机器人进 $groupName($groupId)")
       }
    }
}