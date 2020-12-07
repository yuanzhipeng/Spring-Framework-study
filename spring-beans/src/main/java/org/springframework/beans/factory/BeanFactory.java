/*
 * Copyright 2002-2020 the original author or authors.
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

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * 访问Spring bean 容器的根接口.
 *
 * <p>这是 bean 容器的基本客户端视图;
 * 其他接口，比如 {@link ListableBeanFactory} 和
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}
 * 可用于特定目的的.
 *
 * <p>这个接口由包含许多bean定义的对象实现,
 * 由每个字符串唯一标识. Depending on the bean definition,
 * 工厂将返回包含对象的独立实例
 * (原型设计模式), 或单个共享实例 (上级
 * 替代 单例设计模式 ，其中实例是
 * 工厂范围内的 单例). 返回那种类型的实例
 * 取决于bean工厂的配置: API是相同的. 从Spring
 * 2.0开始, 根据具体的应用，可以使用更多的范围
 * 上下文 (例如 web环境中的"请求"和"会话"范围).
 *
 * <p>这种方法的要点是BeanFactory是一个中央注册中心
 * 应用程序组件, 并集中配置应用程序
 * 组件 (不再需要单个对象读取属性文件,
 * 例如). 请参阅 "Expert One-on-One J2EE Design and
 * Development" 第4章和第11章，以及"发展"为讨论这种方法的好处.
 *
 * <p>注意， 依赖注入通常更好
 * ("push" 配置) 通过setter配置应用程序对象
 * 或构造函数, 而不是使用任何形式"pull"配置 如
 * BeanFactory 查找. Spring的依赖注入功能是
 * 使用此BeanFactory接口及其子接口实现的.
 *
 * <p>通常，BeanFactory将加载存储在配置中的bean定义
 * 源 (如XML文档), 和使用 {@code org.springframework.beans}
 * 用于配置bean的包. 然而, 实现可以简单的返回
 * 它根据需要直接在Java中创建Jaba对象. 没有
 * 如何储存定义的约束: LDAP, RDBMS, XML,
 * 属性文件, 等. 鼓励实现来支持引用
 * 在bean之间 (依赖注入).
 *
 * <p>与{@link ListableBeanFactory}中的方法相比, 所有
 * 此接口的操作还将检查父工厂，如果这是
 * {@link HierarchicalBeanFactory}. 如果在这个工厂实例中找不到bean,
 * 将直接询问父工厂. 此工厂中的bean
 * 应该重写任何父工厂中名字相同的bean.
 *
 * <p>Bean工厂应该尽可能支持标准的Bean生命周期接口.
 * 全套初始化方法及其标准顺序是 :
 * <ol>
 * <li>BeanNameAware's {@code setBeanName}
 * <li>BeanClassLoaderAware's {@code setBeanClassLoader}
 * <li>BeanFactoryAware's {@code setBeanFactory}
 * <li>EnvironmentAware's {@code setEnvironment}
 * <li>EmbeddedValueResolverAware's {@code setEmbeddedValueResolver}
 * <li>ResourceLoaderAware's {@code setResourceLoader}
 * (仅在应用程序上下文中运行时适用)
 * <li>ApplicationEventPublisherAware's {@code setApplicationEventPublisher}
 * (o仅在应用程序上下文中运行时适用)
 * <li>MessageSourceAware's {@code setMessageSource}
 * (仅在应用程序上下文中运行时适用)
 * <li>ApplicationContextAware's {@code setApplicationContext}
 * (仅在应用程序上下文中运行时适用)
 * <li>ServletContextAware's {@code setServletContext}
 * (仅在应用程序上下文中运行时适用)
 * <li>{@code postProcessBeforeInitialization} of BeanPostProcessors 方法
 * <li>InitializingBean's {@code afterPropertiesSet}
 * <li>自定义init方法
 * <li>{@code postProcessAfterInitialization} of BeanPostProcessors 方法
 * </ol>
 *
 * <p>关闭bean工厂时，应用以下生命周期方法:
 * <ol>
 * <li>{@code postProcessBeforeDestruction} of DestructionAwareBeanPostProcessors 方法
 * <li>DisposableBean's {@code destroy}
 * <li>自定义destroy(销毁)方法
 * </ol>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 13 April 2001
 * @see BeanNameAware#setBeanName
 * @see BeanClassLoaderAware#setBeanClassLoader
 * @see BeanFactoryAware#setBeanFactory
 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader
 * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher
 * @see org.springframework.context.MessageSourceAware#setMessageSource
 * @see org.springframework.context.ApplicationContextAware#setApplicationContext
 * @see org.springframework.web.context.ServletContextAware#setServletContext
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization
 * @see InitializingBean#afterPropertiesSet
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getInitMethodName
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization
 * @see DisposableBean#destroy
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName
 */
public interface BeanFactory {

	/**
	 * 用于取消对 {@link FactoryBean} 实例的引用并将其与
	 * 由 <i>created</i> FactoryBean创建的bean. 例如, 如果一个bean的命名
	 * {@code myJndiObject} 是一个FactoryBean, 正在获取 {@code &myJndiObject}
	 * 将返回工厂， 而不是工厂返回的实例.
	 */
	String FACTORY_BEAN_PREFIX = "&";


	/**
	 * 返回一个指定bean的实例，该实例可以是共享的，也可以是独立的.
	 * <p>此方法允许使用Spring BeanFactory作为
	 * 单例或原型设计模式. 调用方可以保留
	 * 对于单例bean，返回的对象.
	 * <p>将别名转换回相应的规范bean名称.
	 * <p>将询问父工厂是否在此工厂实例中找不到bean.
	 * @param name the name of the bean to retrieve
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no bean with the specified name
	 * @throws BeansException if the bean could not be obtained
	 */
	Object getBean(String name) throws BeansException;

	/**
	 * 返回实例, 可以共享或独立, 指定的bean.
	 * <p>其行为与 {@link #getBean(String)} 相同, 但如果bean不是所需的类型
	 * 则通过跑出 BeanNotOfRequiredTypeException 来提供类型安全
	 * 度量. 这意味着 ClassCastException 不能再 casting
	 * 上抛出, 就像可能发生的那样 {@link #getBean(String)}.
	 * <p>将别名转换回相应的规范bean名称.
	 * <p>将询问父工厂是否在此工厂实例中找不到bean.
	 * @param name 要检索的bean的名称
	 * @param requiredType bean必须匹配的类型；可以是接口或超类
	 * @return bean的实例
	 * @throws NoSuchBeanDefinitionException 如果没有这样的bean定义
	 * @throws BeanNotOfRequiredTypeException 如果bean不是所需的类型
	 * @throws BeansException 如果无法创建bean
	 */
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;

	/**
	 * 返回指定bean的一个实例，该实例可以是共享的，也可以是独立的.
	 * <p>允许指定显式构造函数参数/工厂方法参数,
	 * 重写bean定义中指定的默认参数（如果有）.
	 * @param name 要检索的bean的名称
	 * @param args 使用显式参数创建bean实例时要使用的参数
	 * (仅在创建新实例而不是检索现有实例时应用)
	 * @return bean的实例
	 * @throws NoSuchBeanDefinitionException 如果没有这样的bean定义
	 * @throws BeanDefinitionStoreException 如果提供了参数但
	 * 受影响的bean不是原型
	 * @throws BeansException 如果无法创建bean
	 * @since 2.5
	 */
	Object getBean(String name, Object... args) throws BeansException;

	/**
	 * 返回唯一匹配给定对象类型的bean实例（如果有.
	 * <p>此方法按类型查找领域进入{@link ListableBeanFactory}
	 * 但也可以转换为基于
	 * 给定类型名称的常规按名称查找. 对于跨bean集的更广泛的检索操作,
	 * 使用 {@link ListableBeanFactory} 和/或 {@link BeanFactoryUtils}.
	 * @param requiredType bean必须匹配的类型；可以是接口或超类
	 * @return 与所需类型匹配的单个bean实例
	 * @throws NoSuchBeanDefinitionException 如果找不到给定类型的bean
	 * @throws NoUniqueBeanDefinitionException 如果找到多个给定类型的bean
	 * @throws BeansException 如果无法创建bean
	 * @since 3.0
	 * @see ListableBeanFactory
	 */
	<T> T getBean(Class<T> requiredType) throws BeansException;

	/**
	 * 返回指定bean的一个实例，该实例可以是共享的，也可以是独立的.
	 * <p>允许指定显式构造函数参数/工厂方法参数,
	 * 重写bean定义中指定的默认参数（如果有）.
	 * <p>此方法按照类型查找区域j进入 {@link ListableBeanFactory}
	 * 但也可以转换为基于名称的常规按照名称查找
	 * 给定的类型. 对于跨bean集的更广泛的检索操作,
	 * 使用 {@link ListableBeanFactory} 和/或 {@link BeanFactoryUtils}.
	 * @param requiredType bean必须匹配的类型；可以是接口或超类
	 * @param args 使用显式参数创建bean实例时要使用的参数
	 * (仅在创建新实例而不是检索现有实例时应用)
	 * @return bean的实例
	 * @throws NoSuchBeanDefinitionException 如果没有这样的bean定义
	 * @throws BeanDefinitionStoreException 如果提供了参数，
	 * 但受影响的bean不是原型
	 * @throws BeansException 如果无法创建bean
	 * @since 4.1
	 */
	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

	/**
	 * 返回指定bean的提供程序，允许实例的
	 * 延迟按需检索, 包括可用性和唯一性选项.
	 * @param requiredType bean必须匹配的类型；可以是接口或超类
	 * @return 相应的提供程序句柄
	 * @since 5.1
	 * @see #getBeanProvider(ResolvableType)
	 */
	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

	/**
	 * 返回指定bean的提供程序，允许延迟按需检索实例,
	 * 包括可用性和唯一性选项.
	 * @param requiredType bean必须匹配的类型；可以是泛型类型声明.
	 * 注意，这里不支持集合类型，与反射注入点不同.
	 * 以编程方式检索与特定类型匹配的bean列表,
	 * 在这里和后面指定实际的bean类型作为参数
	 * 使用 {@link ObjectProvider#orderedStream()} 或者它的延迟流/迭代选项.
	 * @return 相应的提供程序句柄
	 * @since 5.1
	 * @see ObjectProvider#iterator()
	 * @see ObjectProvider#stream()
	 * @see ObjectProvider#orderedStream()
	 */
	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);

	/**
	 * 这个bean工厂是否包含一个bean定义或外部注册的具有给定名称的singleton实例?
	 * <p>如果给定的名称是别名，它将被转换回相应的规范bean名称.
	 * <p>如果这个工厂是分层的，将询问任何父工厂
	 * 是否在此工厂实例中找不到bean.
	 * <p>如果找到与给定名称匹配的bean定义或singleton实例,
	 * 此方法将返回 {@code true} 命名bean定义是具体的
	 * 或抽象的, 懒惰或渴望, 是否在范围内. 因此, 请注意来自这个方法的 {@code true}
	 * 返回值并不能表示 {@link #getBean}
	 * 将获得同名的实例.
	 * @param name 要查询的bean的名称
	 * @return 是否存在具有给定名称的bean
	 */
	boolean containsBean(String name);

	/**
	 * 这个bean是共享单例的嘛? 也就是说, {@link #getBean} 将
	 * 始终返回相同的实例?
	 * <p>注意: 这个返回 {@code false} 的方法并没有明确的表示
	 * 独立的实例. 它表示非单例实例, 也可能
	 * 对应于作用域Bean. 使用 {@link #isPrototype} 操作显示独立实例
	 * <p>将别名转换回相应的规范bean名称.
	 * <p>将询问父工厂是否在此工厂实例中找不到bean.
	 * @param name 要查询的bean的名称
	 * @return 这个bean是否对应于单例实例
	 * @throws NoSuchBeanDefinitionException 如果没有具有给定名称的bean
	 * @see #getBean
	 * @see #isPrototype
	 */
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 这个bean是原型嘛? 这将始终返回 {@link #getBean}
	 * 独立实例?
	 * <p>注意: 这个返回 {@code false} 的方法并没有清楚地指示
	 * 一个singleton对象. It indicates non-independent instances, 它表示也可能对应于
	 * 作用域bean的非独立实例. 使用 {@link #isSingleton} 操作显示
	 * 检查共享singleton实例.
	 * <p>将别名转换回相应的规范bean名称.
	 * <p>将询问父工厂是否在此工厂实例中找不到bean.
	 * @param name 要查询的bean的名称
	 * @return 这个bean是否总是提供独立的实例
	 * @throws NoSuchBeanDefinitionException 如果没有具有给定名称的bean
	 * @since 2.0.3
	 * @see #getBean
	 * @see #isSingleton
	 */
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 检查具有给定名称的bean是否与指定的类型匹配.
	 * 更具体地说, 检查给定名称的 {@link #getBean} 调用
	 * 调用是否会返回可分配给指定目标类型的对象.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @param typeToMatch the type to match against (as a {@code ResolvableType})
	 * @return {@code true} if the bean type matches,
	 * {@code false} if it doesn't match or cannot be determined yet
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 4.2
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * Check whether the bean with the given name matches the specified type.
	 * More specifically, check whether a {@link #getBean} call for the given name
	 * would return an object that is assignable to the specified target type.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @param typeToMatch the type to match against (as a {@code Class})
	 * @return {@code true} if the bean type matches,
	 * {@code false} if it doesn't match or cannot be determined yet
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.0.1
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * Determine the type of the bean with the given name. More specifically,
	 * determine the type of object that {@link #getBean} would return for the given name.
	 * <p>For a {@link FactoryBean}, return the type of object that the FactoryBean creates,
	 * as exposed by {@link FactoryBean#getObjectType()}. This may lead to the initialization
	 * of a previously uninitialized {@code FactoryBean} (see {@link #getType(String, boolean)}).
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @return the type of the bean, or {@code null} if not determinable
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 1.1.2
	 * @see #getBean
	 * @see #isTypeMatch
	 */
	@Nullable
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Determine the type of the bean with the given name. More specifically,
	 * determine the type of object that {@link #getBean} would return for the given name.
	 * <p>For a {@link FactoryBean}, return the type of object that the FactoryBean creates,
	 * as exposed by {@link FactoryBean#getObjectType()}. Depending on the
	 * {@code allowFactoryBeanInit} flag, this may lead to the initialization of a previously
	 * uninitialized {@code FactoryBean} if no early type information is available.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @param allowFactoryBeanInit whether a {@code FactoryBean} may get initialized
	 * just for the purpose of determining its object type
	 * @return the type of the bean, or {@code null} if not determinable
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 5.2
	 * @see #getBean
	 * @see #isTypeMatch
	 */
	@Nullable
	Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException;

	/**
	 * Return the aliases for the given bean name, if any.
	 * <p>All of those aliases point to the same bean when used in a {@link #getBean} call.
	 * <p>If the given name is an alias, the corresponding original bean name
	 * and other aliases (if any) will be returned, with the original bean name
	 * being the first element in the array.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the bean name to check for aliases
	 * @return the aliases, or an empty array if none
	 * @see #getBean
	 */
	String[] getAliases(String name);

}
