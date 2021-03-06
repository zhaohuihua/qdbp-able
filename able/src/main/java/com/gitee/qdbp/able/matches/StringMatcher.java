/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.gitee.qdbp.able.matches;

/**
 * Copy from org.apache.shiro.util.PatternMatcher.<br>
 * Interface for components that can match source strings against a specified pattern string.
 * <p/>
 * Different implementations can support different pattern types, for example, Ant style path expressions, or
 * regular expressions, or other types of text based patterns.
 *
 * @see org.apache.shiro.util.PatternMatcher
 * @since 0.9 RC2
 */
public interface StringMatcher {

    /** 匹配模式 **/
    enum Matches {
        /** 肯定模式, 符合条件为匹配 **/
        Positive,
        /** 否定模式, 不符合条件为匹配 **/
        Negative
    }
    
    /** 逻辑类型: 多个匹配规则使用and还是or关联 **/
    enum LogicType {
        AND, OR
    }

    /**
     * Returns <code>true</code> if the given <code>source</code> matches the specified <code>pattern</code>,
     * <code>false</code> otherwise.
     *
     * @param source  the source to match
     * @return <code>true</code> if the given <code>source</code> matches the specified <code>pattern</code>,
     *         <code>false</code> otherwise.
     */
    boolean matches(String source);
}
