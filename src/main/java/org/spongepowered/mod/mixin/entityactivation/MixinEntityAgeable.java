/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.spongepowered.mod.mixin.entityactivation;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;
import org.spongepowered.api.entity.living.Ageable;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.SoftOverride;

@NonnullByDefault
@Mixin(EntityAgeable.class)
@Implements(@Interface(iface = Ageable.class, prefix = "ageable$"))
public abstract class MixinEntityAgeable extends EntityCreature {

    private MixinEntityAgeable super$;

    public MixinEntityAgeable(World worldIn) {
        super(worldIn);
    }

    @Shadow
    public abstract int getGrowingAge();

    @Shadow
    public abstract void setGrowingAge(int age);

    @Shadow
    public abstract void setScaleForAge(boolean baby);

    @SoftOverride
    public void inactiveTick() {
        this.super$.inactiveTick();

        if (this.worldObj.isRemote) {
            this.setScaleForAge(this.isChild());
        } else {
            int i = this.getGrowingAge();

            if (i < 0) {
                ++i;
                this.setGrowingAge(i);
            } else if (i > 0) {
                --i;
                this.setGrowingAge(i);
            }
        }
    }

}
