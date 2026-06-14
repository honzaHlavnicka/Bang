package cz.honza.bang.sdk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
// Tady je ta klíčová změna: Přidáváme TYPE a FIELD
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD}) 
public @interface PovolenePluginu {
}