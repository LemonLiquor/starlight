package zhu.moon.mention

import kotlinx.coroutines.launch
import org.quartz.*
import zhu.moon.Plugin

class SimpleJob: Job {
    override fun execute(context: JobExecutionContext?) {
        val mentionStr = context?.trigger?.jobDataMap?.get("string") as String
        Plugin.launch { Mention.sendMsg(1837099861L,mentionStr) }
    }
}