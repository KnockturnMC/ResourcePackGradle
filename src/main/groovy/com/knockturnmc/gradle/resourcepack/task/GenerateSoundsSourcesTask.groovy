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

    Gson GSON = new Gson();

    @TaskAction
    void doTask() throws IOException {
        Map<String, SoundModel> soundModels = GSON.fromJson(new FileReader(this.soundsJson), Map.class);

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
                        .build());

        for (String name : soundModels.keySet()) {
            customSoundsBuilder.addEnumConstant(name.replace("custom.", "").toUpperCase(),
                    TypeSpec.anonymousClassBuilder('$S', name).build());
        }

        TypeSpec customSounds = customSoundsBuilder.build();

        JavaFile javaFile = JavaFile.builder("com.knockturn.resourcepack", customSounds).build();
        javaFile.writeTo(this.outputDir);
    }
}
