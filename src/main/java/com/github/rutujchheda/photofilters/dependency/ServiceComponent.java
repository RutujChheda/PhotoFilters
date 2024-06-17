package com.github.rutujchheda.photofilters.dependency;

import com.github.rutujchheda.photofilters.activity.ConvertPhotoActivity;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component()
public interface ServiceComponent {
    ConvertPhotoActivity provideConvertPhotoActivity();
}