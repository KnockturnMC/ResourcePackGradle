package com.knockturnmc.gradle.resourcepack

import com.knockturnmc.gradle.resourcepack.task.GenerateSoundsSourcesTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ResourcePackPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.with {
            ResourcePackExtension extension = extensions.create('resourcepack', ResourcePackExtension)

            task('generateSoundsSources', type: GenerateSoundsSourcesTask)

            afterEvaluate {
                tasks.generateSoundsSources.with {
                    soundsJson = extension.soundsJson
                    outputDir = extension.outputDir
                }
            }
        }
    }
}
