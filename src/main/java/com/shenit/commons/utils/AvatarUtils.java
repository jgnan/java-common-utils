/***********************************************************************************************************************
 * 
 * Copyright (C) 2013, 2014 by huya (http://www.huya.com)
 * http://www.huya.com/
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/
package com.shenit.commons.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 头像工具类
 * 
 * @author jiangnan
 * 
 */
public final class AvatarUtils {
    private static final String AVATAR_FORMAT = "%s/%s/%s/%s_%d_%d.jpg";
    private static final double AVATAR_RATIO = 4/3d;
    /**
     * Get Avatar URL
     * 
     * @param uid
     * @param prefix
     * @return
     */
    public static String getAvatarURI(Number uid, String prefix) {
        return getAvatarURI(uid, 180, 135, prefix, AvatarIdTypeEnum.UID);
    }

    /**
     * Get Avatar URL
     * 
     * @param uid
     * @param dimen
     * @param prefix
     * @return
     */
    public static String getAvatarURI(Number uid, Number width, String prefix) {
        return getAvatarURI(uid, width, calHeight(width), prefix, AvatarIdTypeEnum.UID);
    }

    /**
     * 计算高度
     * 
     * @param width
     * @return
     */
    private static int calHeight(Number width) {
        return (int) AVATAR_RATIO * width.intValue();
    }

    /**
     * Get Avataer url
     * 
     * @param uid
     * @param width
     * @param height
     * @param prefix
     * @param type
     * @return
     */
    public static String getAvatarURI(Number uid, Number width, Number height,
                    String prefix, AvatarIdTypeEnum type) {
        if (ValidationUtils.all(ValidationUtils.NULL, uid, width, height, prefix)) return null;
        long uidl = uid.longValue();
        String hash = null;
        String path, name;
        if (type == AvatarIdTypeEnum.YY) {
            hash = String.valueOf(hashUid(String.valueOf(uidl), false));
            path = name = String.valueOf(uid);
        } else {
            String hashId = DigestUtils.md5Hex(String.valueOf(uidl));
            hash = String.valueOf(hashUid(hashId, true));
            path = hashId.substring(0, 2);
            name = hashId.substring(2);
        }

        return String.format(AVATAR_FORMAT, prefix, hash, path, name, width.intValue(), height.intValue());
    }

    /**
     * hash uid string
     * 
     * @param id
     * @param isNew
     * @return
     */
    public static long hashUid(String id, boolean isNew) {
        String hash = isNew ? id : DigestUtils.md5Hex(id);
        String result = hash.substring(0, 7);
        long val = Long.parseLong(result, 16) % 100;
        return Math.abs(val) + (isNew ? 1000 : 0);
    }

    public static enum AvatarIdTypeEnum {
        YY, UID
    }
}
