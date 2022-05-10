package zhu.moon.mention

import org.quartz.CronScheduleBuilder.cronSchedule
import org.quartz.JobBuilder
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory
import zhu.moon.ExpandFeature
import zhu.moon.Plugin

object Mention : ExpandFeature() {
    private val bot = Plugin.instanceBot

    //统一命名以避免混淆
    private val content = "string"

    suspend fun sendMsg(qq: Long, msg: String) {
        //PluginMain.targetBotInstance.friends[friendId]?.sendMessage(msg)
        bot.getFriend(qq)?.sendMessage(msg)
    }

    override fun main() {
        val scheduler = StdSchedulerFactory.getDefaultScheduler()

        val job = JobBuilder.newJob(SimpleJob::class.java)
            .withIdentity("mentionJob","jobGroup")
            .storeDurably()
            .build()

        val tipsTrigger = TriggerBuilder.newTrigger()
            .withIdentity("tips","triggerGroup")
            .usingJobData(content,"不要忘记看tips呦 o((>ω< ))o")
            .withSchedule(cronSchedule("0 30 7,11,16 * * ?"))

            .build()

        val trashCanTrigger = TriggerBuilder.newTrigger()
            .withIdentity("trashCan","triggerGroup")
            .usingJobData(content,"轮到你倒垃圾了捏 ε=( o｀ω′)ノ")
            //.withSchedule(cronSchedule("0/10 * * * * ? "))
            // 晚上六点半倒垃圾
            .withSchedule(cronSchedule("0 30 18 * * ?"))
            .forJob(job)
            .build()



        scheduler.scheduleJob(job,tipsTrigger)
        //scheduler.scheduleJob(job,trashCanTrigger)
        //org.quartz.ObjectAlreadyExistsException: Unable to store Job : 'jobGroup.mentionJob', because one already exists with this identification.
        scheduler.scheduleJob(trashCanTrigger)
        scheduler.start()
    }
}