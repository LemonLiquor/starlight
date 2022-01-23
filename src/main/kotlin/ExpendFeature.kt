package zhu.moon

import kotlinx.coroutines.launch

abstract class ExpendFeature  {
    //TODO 一个机器人实例成员变量

    abstract  fun main()

    /**
     * 使用协程启动启动扩展功能
     * 在plugin主线程中启动协程
     */
    fun start(){
        Plugin.launch{ main() }
    }
}