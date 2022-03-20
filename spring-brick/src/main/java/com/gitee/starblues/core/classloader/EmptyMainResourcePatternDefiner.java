/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.core.classloader;

import java.util.Set;

/**
 * 空的 MainResourceDefiner
 * @author starBlues
 * @version 3.0.0
 */
public class EmptyMainResourcePatternDefiner implements MainResourcePatternDefiner {

    @Override
    public Set<String> getIncludePatterns() {
        return null;
    }

    @Override
    public Set<String> getExcludePatterns() {
        return null;
    }
}
