/*
 * This file is part of ResourcePackGradle, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015-2016, KnockturnMC <http://knockturnmc.com/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.knockturnmc.gradle.resourcepack.task;

import com.google.gson.Gson
import com.knockturnmc.gradle.resourcepack.model.SoundModel
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.lang.model.element.Modifier

class GenerateSoundsSourcesTask extends DefaultTask {

    File soundsJson
    File outputDir
    String packageName

    Gson GSON = new Gson()

    @TaskAction
    void doTask() throws IOException {
        Map<String, SoundModel> soundModels = GSON.fromJson(new FileReader(this.soundsJson), Map.class)

        TypeSpec.Builder customSoundsBuilder = TypeSpec.enumBuilder("CustomSound")
                .addModifiers(Modifier.PUBLIC)
                .addField(String.class, "resourceName", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(String.class, "resourceName")
                        .addStatement('this.$N = $N', "resourceName", "resourceName")
                        .build())
                .addMethod(MethodSpec.methodBuilder("getResourceName")
                        .returns(String.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement('return $N', "this.resourceName")
                        .build())

        for (String name : soundModels.keySet()) {
            customSoundsBuilder.addEnumConstant(name.replace("custom.", "").toUpperCase(),
                    TypeSpec.anonymousClassBuilder('$S', name).build())
        }

        TypeSpec customSounds = customSoundsBuilder.build()

        JavaFile javaFile = JavaFile.builder(this.packageName, customSounds)
                .skipJavaLangImports(true)
                .indent("    ")
                .build()
        javaFile.writeTo(this.outputDir)
    }
}
