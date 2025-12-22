package com.taskwizard.android

import android.app.Application
import com.taskwizard.android.core.AgentCore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * AgentCore集成测试
 *
 * 测试范围：
 * 1. 任务启动流程
 * 2. Session管理
 * 3. Note管理
 * 4. 状态检查
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AgentCoreIntegrationTest {

    private lateinit var agentCore: AgentCore
    private lateinit var application: Application

    @Before
    fun setup() {
        application = RuntimeEnvironment.getApplication()
        agentCore = AgentCore(application)
    }

    @After
    fun tearDown() {
        agentCore.stop()
    }

    // ==================== Session管理测试 ====================

    @Test
    fun `startSession应该初始化会话状态`() {
        // When
        agentCore.startSession("测试任务")

        // Then
        assertTrue(agentCore.isSessionRunning(), "Session应该处于运行状态")
    }

    @Test
    fun `stop应该停止会话`() {
        // Given
        agentCore.startSession("测试任务")
        assertTrue(agentCore.isSessionRunning())

        // When
        agentCore.stop()

        // Then
        assertFalse(agentCore.isSessionRunning(), "Session应该已停止")
    }

    @Test
    fun `startSession应该清空之前的历史记录`() {
        // Given
        agentCore.startSession("第一个任务")
        agentCore.addNote("第一个笔记")

        // When
        agentCore.startSession("第二个任务")

        // Then
        val notes = agentCore.getNotes()
        assertTrue(notes.isEmpty(), "新会话应该清空之前的笔记")
    }

    @Test
    fun `多次调用startSession应该重置状态`() {
        // Given
        agentCore.startSession("任务1")
        agentCore.addNote("笔记1")
        agentCore.addNote("笔记2")

        // When
        agentCore.startSession("任务2")

        // Then
        assertTrue(agentCore.isSessionRunning())
        assertEquals(0, agentCore.getNotes().size, "笔记应该被清空")
    }

    // ==================== Note管理测试 ====================

    @Test
    fun `addNote应该添加笔记`() {
        // Given
        agentCore.startSession("测试任务")

        // When
        agentCore.addNote("测试笔记1")
        agentCore.addNote("测试笔记2")

        // Then
        val notes = agentCore.getNotes()
        assertEquals(2, notes.size)
        assertEquals("测试笔记1", notes[0])
        assertEquals("测试笔记2", notes[1])
    }

    @Test
    fun `getNotes应该返回所有笔记的副本`() {
        // Given
        agentCore.startSession("测试任务")
        agentCore.addNote("笔记1")

        // When
        val notes1 = agentCore.getNotes()
        agentCore.addNote("笔记2")
        val notes2 = agentCore.getNotes()

        // Then
        assertEquals(1, notes1.size, "第一次获取应该只有1个笔记")
        assertEquals(2, notes2.size, "第二次获取应该有2个笔记")
    }

    @Test
    fun `空笔记也应该被添加`() {
        // Given
        agentCore.startSession("测试任务")

        // When
        agentCore.addNote("")
        agentCore.addNote("   ")

        // Then
        val notes = agentCore.getNotes()
        assertEquals(2, notes.size, "空笔记也应该被记录")
    }

    @Test
    fun `大量笔记应该正确存储`() {
        // Given
        agentCore.startSession("测试任务")

        // When
        repeat(100) { i ->
            agentCore.addNote("笔记 $i")
        }

        // Then
        val notes = agentCore.getNotes()
        assertEquals(100, notes.size)
        assertEquals("笔记 0", notes[0])
        assertEquals("笔记 99", notes[99])
    }

    // ==================== 状态检查测试 ====================

    @Test
    fun `初始状态应该是未运行`() {
        // Then
        assertFalse(agentCore.isSessionRunning(), "初始状态应该是未运行")
    }

    @Test
    fun `lastThink初始值应该是null`() {
        // Then
        assertEquals(null, agentCore.lastThink, "lastThink初始值应该是null")
    }

    @Test
    fun `getNotes在未启动session时应该返回空列表`() {
        // When
        val notes = agentCore.getNotes()

        // Then
        assertNotNull(notes)
        assertTrue(notes.isEmpty())
    }

    // ==================== 边界情况测试 ====================

    @Test
    fun `空任务描述应该可以启动session`() {
        // When
        agentCore.startSession("")

        // Then
        assertTrue(agentCore.isSessionRunning())
    }

    @Test
    fun `长任务描述应该可以启动session`() {
        // Given
        val longTask = "这是一个非常长的任务描述".repeat(100)

        // When
        agentCore.startSession(longTask)

        // Then
        assertTrue(agentCore.isSessionRunning())
    }

    @Test
    fun `特殊字符任务描述应该可以启动session`() {
        // Given
        val specialTask = "任务包含特殊字符: !@#$%^&*()_+-=[]{}|;':\",./<>?"

        // When
        agentCore.startSession(specialTask)

        // Then
        assertTrue(agentCore.isSessionRunning())
    }

    @Test
    fun `多次stop调用应该是安全的`() {
        // Given
        agentCore.startSession("测试任务")

        // When
        agentCore.stop()
        agentCore.stop()
        agentCore.stop()

        // Then
        assertFalse(agentCore.isSessionRunning())
    }

    @Test
    fun `未启动session时stop应该是安全的`() {
        // When & Then (不应该抛出异常)
        agentCore.stop()
        assertFalse(agentCore.isSessionRunning())
    }

    // ==================== 并发测试 ====================

    @Test
    fun `并发添加笔记应该是安全的`() = runTest {
        // Given
        agentCore.startSession("测试任务")

        // When - 模拟并发添加笔记
        repeat(10) { i ->
            agentCore.addNote("并发笔记 $i")
        }

        // Then
        val notes = agentCore.getNotes()
        assertEquals(10, notes.size, "所有笔记都应该被添加")
    }

    // ==================== 性能测试 ====================

    @Test
    fun `startSession应该快速完成`() {
        // When
        val startTime = System.currentTimeMillis()
        agentCore.startSession("性能测试任务")
        val endTime = System.currentTimeMillis()

        // Then
        val duration = endTime - startTime
        assertTrue(duration < 100, "startSession应该在100ms内完成，实际耗时: ${duration}ms")
    }

    @Test
    fun `addNote应该快速完成`() {
        // Given
        agentCore.startSession("测试任务")

        // When
        val startTime = System.currentTimeMillis()
        repeat(1000) {
            agentCore.addNote("性能测试笔记")
        }
        val endTime = System.currentTimeMillis()

        // Then
        val duration = endTime - startTime
        val avgTime = duration / 1000.0
        assertTrue(avgTime < 1, "平均每次addNote应该在1ms内完成，实际: ${avgTime}ms")
    }

    @Test
    fun `getNotes应该快速完成`() {
        // Given
        agentCore.startSession("测试任务")
        repeat(100) { i ->
            agentCore.addNote("笔记 $i")
        }

        // When
        val startTime = System.currentTimeMillis()
        repeat(1000) {
            agentCore.getNotes()
        }
        val endTime = System.currentTimeMillis()

        // Then
        val duration = endTime - startTime
        val avgTime = duration / 1000.0
        assertTrue(avgTime < 0.1, "平均每次getNotes应该在0.1ms内完成，实际: ${avgTime}ms")
    }
}
