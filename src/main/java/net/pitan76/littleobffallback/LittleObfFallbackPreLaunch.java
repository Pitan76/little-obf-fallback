package net.pitan76.littleobffallback;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.pitan76.littleobffallback.transformer.ClassFileTransformerImpl;
import org.objectweb.asm.*;

import java.lang.instrument.Instrumentation;

public class LittleObfFallbackPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        Instrumentation inst = ByteBuddyAgent.install();
        System.out.println("[LittleObfFallback] PreLaunch: Instrumentation attached");
        inst.addTransformer(new ClassFileTransformerImpl());
        System.out.println("[LittleObfFallback] Global ASM Transformer injected successfully.");
    }
}