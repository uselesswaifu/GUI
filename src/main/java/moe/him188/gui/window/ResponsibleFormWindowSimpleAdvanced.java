package moe.him188.gui.window;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import com.google.gson.annotations.Expose;
import moe.him188.gui.window.listener.response.AdvancedSimpleResponseListener;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@link ResponsibleFormWindowSimple} 高级版(2333). <br/>
 * Advanced {@link ResponsibleFormWindowSimple} <br/>
 * 高级版无需通过 id 自找数据, 一切的一切都由 GUI 帮你完成. 你只需要实现后续处理即可! <br/>
 * You needn't get entries from id that is responded in {@link ResponsibleFormWindowSimple#onClicked}, but everything will be done by GUI. <br>
 * <br/>
 * 普通版里面, 点击事件函数参数1是 int, 是按钮的 ID. <br/>
 * In Normal, type of click event function parameter 1 is int, which represents the button ID.  <br>
 * 并且需要使用 Simple 类型表单的情况, 一般是有一组同一类型的数据需要依次显示. 比如传送世界列表, 任务列表 <br/>
 * And when you are using the {@link FormWindowSimple}, generally it's time of a group of the same type of data need to show. Such as teleport positions, missions lists. <br>
 * 在普通版构造器中, 你需要手动对数据遍历并添加按钮, 在普通版click事件中, 你又需要手动从数据({@link List},{@link Map}等)中取出第 n 项, 再处理. <br/>
 * In normal constructors, you should foreach your Collections add add buttons. <br>
 * In normal click functions, you should get entry(value) from Collections. <br>
 * 如果再来一个窗口, 你又要枯燥地重复一切. 那么是时候使用高级版了! <br/>
 * In another window, you need to repeat everything again, that's too boring! At this time, you should use Advanced. <br>
 * <br/>
 * 高级版里面, 由java泛型支持, 你在构造器中需要传入一组数据和构造按钮的函数, 然后在点击事件中直接拿到按钮对应的数据进行处理 <br>
 * In Advanced, with the support of java Generics, you can get entry directly in click events. <br>
 * <p>
 * 普通版要这么做: <br>
 * In Normal: <br>
 * <pre>
 * List<Publisher> data = new ArrayList<>();
 * window = new 普通版(title, content){
 *     for (publisher: data){
 *         addButton(new ElementButton(publisher.getName()));
 *     }
 *
 *     onClicked((id, player) -> {
 *         Publisher publisher = data.get(id);
 *         //coding
 *     })
 * };
 * </pre>
 * 高级版只要这么做: <br>
 * In Advanced: <br>
 * <pre>
 * window = new 高级版(title, content, data, Publisher::getName).onClicked((publisher, player) -> {
 *     //coding
 * });
 * </pre>
 *
 * @author Him188moe @ ProjectARK Project
 */
public class ResponsibleFormWindowSimpleAdvanced<E> extends MarkedFormWindowSimple {
    /**
     * 用于 {@linkplain #onEvent(PlayerFormRespondedEvent) 收到返回数据时获取实例}
     */
    private static Map<Integer, ResponsibleFormWindowSimpleAdvanced<?>> instances = new HashMap<>();

    @Expose(serialize = false, deserialize = false)
    private BiConsumer<E, Player> buttonClickedListener = null;

    @Expose(serialize = false, deserialize = false)
    private Consumer<Player> windowClosedListener = null;

    @Expose(serialize = false, deserialize = false)
    private final List<E> entries;

    public List<E> getEntries() {
        return entries;
    }

    public E getEntry(int id) {
        return this.entries.get(id);
    }

    /**
     * @param title            标题
     * @param content          内容
     * @param entries          需要展示在每个按钮上的数据 | entries to show in the buttons
     * @param buttonTextGetter 按钮名字获取器. 用于获取每个数据对应的按钮的名字 | Used to get the name of each button
     */
    public ResponsibleFormWindowSimpleAdvanced(String title, String content, Collection<E> entries, Function<? super E, String> buttonTextGetter) {
        super(Objects.requireNonNull(title), Objects.requireNonNull(content));
        Objects.requireNonNull(buttonTextGetter);
        Objects.requireNonNull(entries);

        for (E entry : entries) {
            this.addButton(new ElementButton(buttonTextGetter.apply(entry)));
        }
        this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
        instances.put(this.getId(), this);
    }

    @Expose(serialize = false, deserialize = false)
    private boolean singleUse = true;

    public boolean isSingleUse() {
        return singleUse;
    }

    /**
     * 标识是否只能使用一次这个表单对象. <br>
     * Identify whether you can use(send) the form object only once. <br>
     * 如果 true, 第二次将这个表单对象发送给玩家时将无法接受 response. <br>
     * If true, when you send the form twice, it will not be able to accept the response <br>
     * 如果 false, 每次都能正常接受 response. <br>
     * If false, you can send the form every time, and successfully receive the response <br>
     * 设置为 true 将会更适合长时间运行服务器. <br>
     * True is better for long-time working.
     *
     * @param singleUse single-use
     *
     * @return this
     */
    public ResponsibleFormWindowSimpleAdvanced<E> setSingleUse(boolean singleUse) {
        this.singleUse = singleUse;
        return this;
    }

    /**
     * 判断这个窗口是否还可以正常接受 response. <br>
     * Check whether this window can receive response.
     *
     * @return valid or not
     *
     * @see #setSingleUse(boolean)
     */
    public boolean isValid() {
        if (!this.isSingleUse()) {
            return true;
        }

        return instances.containsKey(this.getId());
    }

    /**
     * 在玩家提交表单后调用 <br>
     * Called on submitted
     *
     * @param listener 调用的方法
     */
    public ResponsibleFormWindowSimpleAdvanced<E> onClicked(BiConsumer<E, Player> listener) {
        this.buttonClickedListener = listener;
        return this;
    }

    /**
     * 在玩家提交表单后调用 <br>
     * Called on submitted
     *
     * @param listener 调用的方法(无 Player)
     */
    public ResponsibleFormWindowSimpleAdvanced<E> onClicked(Consumer<E> listener) {
        Objects.requireNonNull(listener);
        this.buttonClickedListener = (response, player) -> listener.accept(response);
        return this;
    }

    /**
     * 在玩家关闭窗口而没有点击按钮提交表单后调用. <br>
     * Called on closed without submitting.
     *
     * @param listener 调用的方法
     */
    public ResponsibleFormWindowSimpleAdvanced<E> onClosed(Consumer<Player> listener) {
        this.windowClosedListener = listener;
        return this;
    }

    /**
     * 在玩家关闭窗口而没有点击按钮提交表单后调用. <br>
     * Called on closed without submitting.
     *
     * @param listener 调用的方法
     */
    public ResponsibleFormWindowSimpleAdvanced<E> onClosed(Runnable listener) {
        Objects.requireNonNull(listener);
        this.windowClosedListener = (player) -> listener.run();
        return this;
    }

    @Override
    public void addButton(ElementButton button) {
        if (this.entries != null) {
            throw new UnsupportedOperationException("could not addButton after construction!");
        }
    }

    public void callClicked(E entry, Player player) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(entry);
        this.buttonClickedListener.accept(entry, player);
    }

    public void callClosed(Player player) {
        Objects.requireNonNull(player);
        this.windowClosedListener.accept(player);
    }

    @SuppressWarnings("unchecked")
    public static boolean onEvent(PlayerFormRespondedEvent event) {
        if (event.getWindow() instanceof MarkedFormWindowSimple && event.getResponse() instanceof FormResponseSimple) {
            int id = ((MarkedFormWindowSimple) event.getWindow()).getId();
            ResponsibleFormWindowSimpleAdvanced window = instances.get(id);
            if (window.isSingleUse()) {
                instances.remove(id);
            }
            if (window instanceof AdvancedSimpleResponseListener) {
                ((AdvancedSimpleResponseListener) window).onClicked((FormResponseSimple) event.getResponse(), event.getPlayer());
            }

            if (event.getWindow().wasClosed() || event.getResponse() == null) {
                if (window.windowClosedListener == null) {
                    return true;
                }
                window.callClosed(event.getPlayer());
            } else {
                if (window.buttonClickedListener == null || event.getResponse() == null) {
                    return true;
                }
                window.callClicked(window.getEntry(((FormResponseSimple) event.getResponse()).getClickedButtonId()), event.getPlayer());
            }

            return true;
        }
        return false;
    }

}
