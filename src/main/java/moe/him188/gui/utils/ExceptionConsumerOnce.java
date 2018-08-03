package moe.him188.gui.utils;

import cn.nukkit.Player;

import java.lang.reflect.Array;

/**
 * 收到第一个异常时立即处理, 随后忽略所有异常
 *
 * @author Him188moe @ GUI Project
 */
public abstract class ExceptionConsumerOnce<E extends Exception> implements ExceptionConsumer<E> {
    private boolean accepted = false;

    @SuppressWarnings("unchecked")
    @Override
    public void catchException(Player player, E e) {
        if (!accepted) {
            accepted = true;
            this.accept(player, (E[]) Array.newInstance(e.getClass().getComponentType(), 1));
        }
    }
}
