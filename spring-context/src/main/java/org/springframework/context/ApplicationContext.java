/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.context;

import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;

/**
 * 为应用程序提供配置的中央接口.
 * 在应用程序运行时，这是只读的，但可能是
 * 如果实现支持此功能，则重新加载.
 *
 * <p> ApplicationContext 提供:
 * <ul>
 * <li>访问应用程序组件的Bean工厂方法.
 * 从 {@link org.springframework.beans.factory.ListableBeanFactory} 继承.
 * <li>以通用方式加载文件资源的能力.
 * 继承自 {@link org.springframework.core.io.ResourceLoader} 接口.
 * <li>向注册的侦听器发布事件的能力.
 * 继承自 {@link ApplicationEventPublisher} 接口.
 * <li>能够解析消息，支持国际化.
 * 继承自 {@link MessageSource} 接口.
 * <li>从父上下文继承。后代上下文中的定义
 * 总是优先考虑。这意味着，例如，单亲父母
 * 上下文可以被整个web应用程序使用，而每个servlet都有
 * 它自己的子上下文，独立于任何其他servlet的子上下文.
 * </ul>
 *
 * <p>除了标准 {@link org.springframework.beans.factory.BeanFactory}
 * 实现和调用ApplicationContext功能
 * {@link ApplicationContextAware} beans 还有 {@link ResourceLoaderAware},
 * {@link ApplicationEventPublisherAware} and {@link MessageSourceAware} beans.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see ConfigurableApplicationContext
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.core.io.ResourceLoader
 */
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
		MessageSource, ApplicationEventPublisher, ResourcePatternResolver {

	/**
	 * 返回此应用程序上下文的唯一id.
	 * @return the unique id of the context, or {@code null} if none
	 */
	@Nullable
	String getId();

	/**
	 * 返回此上下文所属的已部署应用程序的名称.
	 * @return a name for the deployed application, or the empty String by default
	 */
	String getApplicationName();

	/**
	 * 返回此上下文的友好名称.
	 * @return a display name for this context (never {@code null})
	 */
	String getDisplayName();

	/**
	 * 返回首次加载此上下文时的时间戳.
	 * @return the timestamp (ms) when this context was first loaded
	 */
	long getStartupDate();

	/**
	 * 返回父上下文，如果没有父上下文，则返回{@code null}
	 * 这是上下文层次结构的根.
	 * @return the parent context, or {@code null} if there is no parent
	 */
	@Nullable
	ApplicationContext getParent();

	/**
	 * 为此上下文公开AutowireCapableBeanFactory功能.
	 * <p>这通常不被应用程序代码使用，除非用于
	 * 初始化位于应用程序上下文之外的bean实例,
	 * 对它们应用springbean生命周期（全部或部分）.
	 * <p>或者, 由
	 * {@link ConfigurableApplicationContext} 接口提供对
	 * {@link AutowireCapableBeanFactory} 接口也是. 目前的方法主要有
	 * 作为ApplicationContext接口上的一个方便、特定的工具.
	 * <p><b>注意: 从4.2开始，在应用程序上下文关闭之后
	 * 这个方法将始终抛出 illeglastateException.</b> 在当前版本的Spring框架中
	 * 只有可刷新的应用程序上下文才会这样; 从4.2开始,
	 * 所有应用程序上下文实现都将被要求遵守.
	 * @return 自动装配
	 * @throws IllegalStateException 如果上下文不支持
	 * {@link AutowireCapableBeanFactory} 接口, 或者不持有
	 * 支持自动连线的bean工厂 (例如 if {@code refresh()} 有
	 * 从未调用), 或者上下文已经关闭
	 * @see ConfigurableApplicationContext#refresh()
	 * @see ConfigurableApplicationContext#getBeanFactory()
	 */
	AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;

}
