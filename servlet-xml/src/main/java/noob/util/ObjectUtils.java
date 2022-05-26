package noob.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class ObjectUtils {
    /**
     * 确定给定对象是否为空。
     * 此方法支持以下对象类型。
     * <ul>
     * <li>{@code Optional}：如果不是 {@link Optional#isPresent()} 则视为空</li>
     * <li>{@code Array}：如果其长度为零则视为空</li>
     * <li>{@link CharSequence}：如果它的长度为零则认为是空的</li>
     * <li>{@link Collection}：代表 {@link Collection#isEmpty()}</li>
     * <li>{@link Map}：代表 {@link Map#isEmpty()}</li>
     *  </ul>
     * 如果给定对象为非 null 且不是上述支持的类型之一，则此方法返回 false。
     * @param obj   要检测的对象
     * @return  如果对象为 null 或为空，则为 true
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        /**
         * Java 8的Optional类
         */
        if (obj instanceof Optional) {
            return !((Optional<?>) obj).isPresent();
        }
        /**
         * 字符串的超类
         */
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        /**
         * 数组
         */
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        /**
         * list集合
         */
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        /**
         * map集合
         */
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }
        return false;
    }
}
