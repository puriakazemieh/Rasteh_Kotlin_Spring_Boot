package com.kazemieh.rasteh.story.api

import com.kazemieh.rasteh.story.application.StoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stories")
class StoryController(
    private val storyService: StoryService
) {
    @GetMapping
    fun getActiveStories() = storyService.getActiveStories()
}
