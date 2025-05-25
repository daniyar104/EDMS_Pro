package com.example.docplatform.logging;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentAction {
    String type();         // Тип действия, например: ACCEPTED, REJECTED и т.д.
    String description() default ""; // Доп. описание (по желанию)
}
