package com.knockturnmc.gradle.resourcepack.model

class SoundModel {

    String category
    List<Sound> sounds

    class Sound {

        String name
        boolean stream
    }
}
