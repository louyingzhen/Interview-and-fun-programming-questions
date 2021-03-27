package Spring源码解析;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import javax.jws.Oneway;
import javax.print.DocFlavor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.DrbgParameters;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.LinkedHashSet;
import java.util.Set;

public class CreateBean {

    protected final Log logger = LogFactory.getLog(getClass());

    protected Object createBean(
            final String beanName, final RootBeanDefinition mbd, final Object[] args)
            throws BeanCreationException {
        if (logger.isDebugEnabled()) {
            logger.debug("Create instance of bean '" + beanName + "'");
        }

        // 锁定class，根据设置的class属性或者根据className来解析Class
        resolveBeanClass(mbd, beanName);
        // 验证及准备覆盖的方法

        try {
            mbd.prepareMethodOverrides();
        } catch (BeanDefinitionValidationException ex) {
            throw new BeanDefinitionStoreException(
                    mbd.getResourceDescription(),
                    beanName,
                    "Validation of method overrides failed",
                    ex);
        }

        try {
            // 给BeanPostProcessors一个机会来返回代理来替代真正的实例
            Object bean = resolveBeforeInstantiation(beanName, mbd);

            // 当经过前置处理后返回的结果如果不为空，那么会直接略过后续的bean的创建而直接返回结果。
            // 这一特性虽然很容易被忽略，但是却起着至关重要的作用，我们熟知的AOP功能就是基于这里的判断的
            if (bean != null) {
                return bean;
            }
        } catch (Throwable ex) {
            throw new BeanCreationException(
                    mbd.getResourceDescription(),
                    beanName,
                    "BeanPostProcessor before instantiation of bean failed",
                    ex);
        }

        Object beanInstance = doCreateBean(beanName, mbd, args);

        if (logger.isDebugEnabled()) {
            logger.debug("Finished creating instance of bean '" + beanName + "'");
        }
        return beanInstance;
    }

    @Nullable
    protected Class<?> resolveBeanClass(
            final RootBeanDefinition mbd, String beanName, final Class<?>... typesToMatch)
            throws CannotLoadBeanClassException {

        try {
            if (mbd.hasBeanClass()) {
                return mbd.getBeanClass();
            }
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged(
                        (PrivilegedExceptionAction<Class<?>>)
                                () -> doResolveBeanClass(mbd, typesToMatch),
                        getAccessControlContext());
            } else {
                return doResolveBeanClass(mbd, typesToMatch);
            }
        } catch (PrivilegedActionException pae) {
            ClassNotFoundException ex = (ClassNotFoundException) pae.getException();
            throw new CannotLoadBeanClassException(
                    mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex);
        } catch (ClassNotFoundException ex) {
            throw new CannotLoadBeanClassException(
                    mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex);
        } catch (LinkageError err) {
            throw new CannotLoadBeanClassException(
                    mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), err);
        }
    }

    protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
        Object bean = null;
        // 如果尚未被解析
        if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
            // Make sure bean class is actually resolved at this point.
            if (mbd.hasBeanClass()
                    && !mbd.isSynthetic()
                    && hasInstantiationAwareBeanPostProcessors()) {
                bean = applyBeanPostProcessorsBeforeInstantiation(mbd.getBeanClass(), beanName);
                if (bean != null) {
                    bean = applyBeanPostProcessorsAfterInstantiation(bean, beanName);
                }
            }
            mbd.beforeInstantiationResolved = (bean != null);
        }
        return bean;
    }

    protected Object applyBeanPostProcessorsBeforeInstantiation(Class beanClass, String beanName)
            throws BeansException {
        for (BeanPostProcessor bp : getBeanPostProcessors()) {
            if (bp instanceof InstantiationAwareBeanPostProcessor) {
                InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
                Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public Object applyBeanPostProcessorsAfterInstantiation(Object existingBean, String beanName)
            throws BeansException {
        Object result = existingBean;

        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            result = beanPostProcessor.postProcessAfterInitialization(result, beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }

    protected Object doCreateBean(
            final String beanName, final RootBeanDefinition mbd, final Object[] args) {
        // Instantiate the bean
        BeanWrapper instanceWrapper = null;
        if (mbd.isSingleton()) {
            instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
        }

        if (instanceWrapper == null) {
            // 根据指定bean使用对应的策略创建新的实例，如：工厂方法、构造函数自动注入、简单初始化
            instanceWrapper = createBeanInstance(beanName, mbd, args);
        }
        final Object bean = (instanceWrapper != null ? instanceWrapper.getWrappedInstance() : null);

        Class beanType = (instanceWrapper != null ? instanceWrapper.getWrappedClass() : null);

        // Allow post-processors to modify the merged bean definition

        synchronized (mbd.postProcessingLock) {
            if (!mbd.postProcessed) {
                // 应用MergedBeanDefinitionPostProcessor
                applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
                mbd.postProcessed = true;
            }
        }

        /** 是否需要提早曝光：单例&允许循环依赖&当前bean正在创建中，检测循环依赖 */
        boolean earlySingletonExposure =
                ((mbd.isSingleton())
                        && this.allwoCircularReferences
                        && isSingletonCurrentlyInCreation(beanName));

        if (earlySingletonExposure) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "Eagerly caching bean '"
                                + beanName
                                + "' to allow for resolving potential circular references");
            }
            // 为避免后期循环依赖，可以在bean初始化完成前将创建实例的ObjectFactory加入工厂
            addSingletonFactory(
                    beanName,
                    new ObjectFactory() {
                        public Object getObject() throws BeansException {
                            // 对bean再一次依赖引用，主要应用SmartInstantiationAwareBeanPostProcessor
                            // 其中我们熟知的AOP就是在这里将advice动态织入bean中，若没有则直接返回bean，不做任何处理
                            return getEarlyBeanReference(beanName, mbd, bean);
                        }
                    });
        }

        // Initialize the bean instance
        Object exposedObject = bean;

        try {
            // 对bean进行填充，将各个属性注入，其中，可能存在依赖于其他bean的属性，则会递归初始依赖bean
            populateBean(beanName, mbd, instanceWrapper);

            if (exposedObject != null) {
                // 调用初始化方法，比如init-method
                exposedObject = initializeBean(beanName, exposedObject, mbd);
            }
        } catch (Throwable ex) {

        }

        if (earlySingletonExposure) {
            Object earlySingletonReference = getSingleton(beanName, false);
            // earlySingletonReference只有在检测到有循环依赖的情况下才会不为空
            if (earlySingletonReference != null) {
                // 如果exposedObject没有在初始化方法中被改变，也就是没有被增强
                if (exposedObject == bean) {
                    exposedObject = earlySingletonReference;
                } else if (!this.allowRawInjectionDespiteWrapping && hasDepentBean(beanName)) {
                    String[] dependentBeans = getDependentBeans(beanName);
                    Set<String> actualDependentBeans =
                            new LinkedHashSet<String>(dependentBeans.length);
                    for (String dependentBean : dependentBeans) {
                        // 检测依赖
                        if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
                            actualDependentBeans.add(dependentBean);
                        }
                    }
                    /*
                     * 因为bean创建后其所依赖的bean一定是已经创建的，
                     * actualDependentBeans不为空则表示当前bean创建后其依赖的bean却没有全部创建完，也就是说存在循环依赖
                     */
                    if (!actualDependentBeans.isEmpty()) {
                        // 抛出异常
                    }
                }
            }
        }

        //  Register bean as disposable

        try {
            // 根据scopse注册bean
            registerDisposableBeanIfNecessary(beanName, bean, mbd);
        } catch (BeanDefinitionValidationException ex) {
            throw new BeanCreationException(
                    mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex);
        }
        return exposedObject;
    }

    protected BeanWrapper createBeanInstance(
            String beanName, RootBeanDefinition mbd, Object[] args) {
        // 解析class
        Class beanClass = resolveBeanClass(mbd, beanName);
        if (beanClass != null
                && !Modifier.isPublic(beanClass.getModifiers())
                && !mbd.isNonPublicAccessAllowed()) {
            throw new BeanCreationException(
                    mbd.getResourceDescription(),
                    beanName,
                    "Bean class isn't public,and non-public access not allowed:"
                            + beanClass.getName());
        }

        // 如果工厂方法不为空则使用工厂方法初始化策略
        if (mbd.getFactoryMethodName() != null) {
            return instantiateUsingFactoryMethod(beanName, mbd, args);
        }

        // Shortcut when re-creating the same bean...
        boolean resolved = false;
        boolean autowireNecessary = false;

        if (args == null) {
            synchronized (mbd.constructorArgumentLock) {
                // 一个类有多个构造函数，每个构造函数都有不同的参数，所以调用前
                // 需要先根据参数锁定构造函数或对应的工厂方法
                if (mbd.resolvedConstructorOrFactoryMethod != null) {
                    resolved = true;
                    autowireNecessary = mbd.constructorArgumentsResolved;
                }
            }
        }
        // 如果已经解析过则使用解析好的构造函数方法不需要再次锁定
        if (resolved) {
            if (autowireNecessary) {
                // 构造自动注入
                return autowireConstructor(beanName, mbd, null, null);
            } else {
                // 使用默认构造函数构造
                return instantiateBean(beanName, mbd);
            }
        }

        // 需要根据参数解析构造函数
        Constructor[] ctors = determineConstructordFromBeanPostProcessors(beanClass, beanName);

        if (ctors != null
                || mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_CONSTRUCTOR
                || mbd.hasConstructorArgumentValues()
                || !ObjectUtils.isEmpty(args)) {
            // 构造函数自动注入
            return autowireConstructor(beanName, mbd, ctors, args);
        }

        // 使用默认构造函数构造
        return instantiateBean(beanName, mbd);
    }
}
