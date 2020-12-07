/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory;

import org.springframework.lang.Nullable;

/**
 * 子接口由bean工厂实现,可以是层次结构的一部分
 *
 * <p> bean对应的 {@code setParentBeanFactory} 方法
 * 允许在可配置的
 * 可以在ConfigurableBeanFactory接口中找到.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 07.07.2003
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#setParentBeanFactory
 */
public interface HierarchicalBeanFactory extends BeanFactory {

	/**
	 * 返回父bean工厂，如果没有父bean工厂，则返回{@code null}.
	 */
	@Nullable
	BeanFactory getParentBeanFactory();

	/**
	 * 返回本地bean工厂是否包含给定名称的bean，
	 * 忽略祖先上下文中定义的bean.
	 * <p>这是 {@code containsBean}, 的替代方法，忽略bean
	 * 来自一个豆工厂的名字.
	 * @param name 要查询的bean的名称
	 * @return 是否在本地工厂中定义了具有给定名称的bean
	 * @see BeanFactory#containsBean
	 */
	boolean containsLocalBean(String name);

}
