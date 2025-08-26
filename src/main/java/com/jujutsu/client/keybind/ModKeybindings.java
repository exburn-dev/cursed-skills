package com.jujutsu.client.keybind;

import com.jujutsu.Jujutsu;
import com.jujutsu.screen.AbilityUpgradesScreen;
import com.jujutsu.systems.ability.AbilitySlot;
import com.jujutsu.event.client.KeyEvents;
import com.jujutsu.network.payload.AbilityKeyPressedPayload;
import com.jujutsu.screen.AbilitiesKeybindingsScreen;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import com.jujutsu.systems.ability.upgrade.AbilityUpgradesReloadListener;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModKeybindings {
    public static final KeyBinding OPEN_ABILITIES_KEYBINDING_SCREEN = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.jujutsu.open_abilities_keybindings", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "category.jujutsu.jujutsu"));
    public static final KeyBinding OPEN_ABILITY_UPGRADES_SCREEN = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.jujutsu.open_ability_upgrades", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, "category.jujutsu.jujutsu"));

    public static final List<AbilityKeyBinding> abilityBindings = new ArrayList<>();
    private static final HashMap<KeyBinding, Boolean> wasPressed = new HashMap<>();

    public static final KeyBinding ABILITY_1_KEY = registerAbilityKeyBinding(new AbilityKeyBinding(AbilitySlot.ABILITY_SLOT_1,"key.jujutsu.ability_1", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.jujutsu.jujutsu"));
    public static final KeyBinding ABILITY_2_KEY = registerAbilityKeyBinding(new AbilityKeyBinding(AbilitySlot.ABILITY_SLOT_2, "key.jujutsu.ability_2", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.jujutsu.jujutsu"));
    public static final KeyBinding ABILITY_3_KEY = registerAbilityKeyBinding(new AbilityKeyBinding(AbilitySlot.ABILITY_SLOT_3, "key.jujutsu.ability_3", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Y, "category.jujutsu.jujutsu"));
    public static final KeyBinding ABILITY_4_KEY = registerAbilityKeyBinding(new AbilityKeyBinding(AbilitySlot.ABILITY_SLOT_4, "key.jujutsu.ability_4", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "category.jujutsu.jujutsu"));

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(OPEN_ABILITIES_KEYBINDING_SCREEN.wasPressed()) {
                client.setScreen(new AbilitiesKeybindingsScreen());
            }
            else if(OPEN_ABILITY_UPGRADES_SCREEN.wasPressed()) {
                IAbilitiesHolder holder = (IAbilitiesHolder) client.player;
                Identifier upgradesId = holder.getUpgradesId();

                Jujutsu.LOGGER.info(holder.getUpgradesId().toString());
                Jujutsu.LOGGER.info(AbilityUpgradesReloadListener.getInstance().getBranchesIds().toString());
                Jujutsu.LOGGER.info("contains {}", AbilityUpgradesReloadListener.getInstance().getBranchesIds().contains(upgradesId));

                if(AbilityUpgradesReloadListener.getInstance().getBranchesIds().contains(upgradesId)) {
                    client.setScreen(new AbilityUpgradesScreen(AbilityUpgradesReloadListener.getInstance().getBranches(upgradesId)));
                }
            }
        });

        KeyEvents.KEY_PRESSED_EVENT.register((client, key, scanCode) -> {
            if (client.currentScreen != null || !AdditionalInputSystem.isEmpty()) return ActionResult.PASS;

            for(AbilityKeyBinding binding: abilityBindings) {
                if(binding.matchesKey(key, scanCode) && (!wasPressed.containsKey(binding) || !wasPressed.get(binding))) {
                    ClientPlayNetworking.send(new AbilityKeyPressedPayload(binding.getAbilitySlot(), false));
                    wasPressed.put(binding, true);
                    break;
                }
            }

            return ActionResult.PASS;
        });

        KeyEvents.KEY_RELEASED_EVENT.register((client, key, scanCode) -> {
            if (client.currentScreen != null || !AdditionalInputSystem.isEmpty()) return ActionResult.PASS;

            for(AbilityKeyBinding binding: abilityBindings) {
                if(binding.matchesKey(key, scanCode)) {
                    ClientPlayNetworking.send(new AbilityKeyPressedPayload(binding.getAbilitySlot(), true));
                    wasPressed.put(binding, false);
                    break;
                }
            }

            return ActionResult.PASS;
        });
    }

    private static AbilityKeyBinding registerAbilityKeyBinding(AbilityKeyBinding binding) {
        abilityBindings.add(binding);
        return binding;
    }
}
