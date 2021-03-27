/*
package Spring源码解析;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import sun.awt.Mutex;

import java.awt.color.ProfileDataException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.beans.factory.BeanFactoryUtils.originalBeanName;
import static org.springframework.beans.factory.BeanFactoryUtils.transformedBeanName;
import static sun.tools.jconsole.inspector.XObject.NULL_OBJECT;

public class BeanFactory {

    */
/** Map from scope identifier String to corresponding Scope. *//*

    private final Map<String, Scope> scopes = new LinkedHashMap<String, Scope>(8);

    */
/** Cache of singleton objects: bean name to bean instance. *//*

    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>(256);

    */
/** Cache of early singleton objects: bean name to bean instance. *//*

    private final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>(16);

    */
/** Cache of singleton factories: bean name to ObjectFactory. *//*

    private final Map<String, ObjectFactory<?>> singletonFactories =
            new HashMap<String, ObjectFactory<?>>(16);

    */
/** Cache of singleton objects created by FactoryBeans: FactoryBean name to object. *//*

    private final Map<String, Object> factoryBeanObjectCache =
            new ConcurrentHashMap<String, Object>(16);

    */
/** Flag that indicates whether we're currently within destroySingletons. *//*

    private boolean singletonsCurrentlyInDestruction = false;

    */
/** List of suppressed Exceptions, available for associating related causes. *//*

    @Nullable private Set<Exception> suppressedExceptions;

    */
/** Names of beans that are currently in creation. *//*

    private final Set<String> singletonsCurrentlyInCreation =
            Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    */
/** Names of beans currently excluded from in creation checks. *//*

    private final Set<String> inCreationCheckExclusions =
            Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    */
/** Logger available to subclasses. *//*

    protected final Log logger = LogFactory.getLog(getClass());

    */
/** Set of registered singletons, containing the bean names in registration order. *//*

    private final Set<String> registeredSingletons = new LinkedHashSet<>(256);

    public Object getBean(String name) throws BeansException {
        return doGetBean(name, null, null, false);
    }

    protected <T> T doGetBean(
            final String name,
            final Class<T> requiredType,
            final Object[] args,
            boolean typeCheckOnly)
            throws BeansException {

        // 提取对应的beanName
        final String beanName = transformedBeanName(name);

        Object bean;

        */
/**
         * 检查缓存中或者实例工厂中是否有对应的实例 为什么会首先使用这段代码呢，因为在创建单例bean的时候会存在依赖注入的情况，而创建依赖的时候，为了避免循环依赖，
         * Spring创建bean的原则是不等bean创建完成就会将bean的ObjectFactory提早曝光
         * 也就是将ObjectFactory加入到缓存中，一旦下个bean创建的时候需要依赖上个bean则直接使用beanFactory
         *//*

        // 尝试直接从缓存获取或者singletonFactories中的ObjectFactory中获取
        Object sharedInstance = getSingleton(beanName);

        if (sharedInstance != null && args == null) {
            if (logger.isDebugEnabled()) {
                if (isSingletonCurrentlyInCreation(beanName)) {
                    logger.debug(
                            "Returning eagerly cached instance of singleton bean"
                                    + beanName
                                    + "that is not fully initialized yet - a consequence of circular reference");
                } else {
                    logger.debug("Returning cached instance of singletoon bean '" + beanName + "'");
                }
            }
            // 返回对应的实例，有时候存在诸如BeanFactory的情况并不是直接返回实例本身而是返回指定方法返回的实例
            bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
        } else {

            // 只有在单例情况才会尝试解决循环依赖，原型模式情况下，如果存在A中有B的属性，B中有A的属性，
            // 那么当依赖注入的时候，就会产生当A还未创建完的时候因为对于B的创建再次返回创建A。造成循环依赖，也就是下面这种情况
            // isPrototypeCurrentlyInCreation(beanName)为true
            if (isPrototypeCurrentlyInCreation(beanName)) {
                throw new BeanCurrentlyInCreationException(beanName);
            }

            BeanFactory parentBeanFactory = getParentBeanFactory();
            // 如果beanDefinitionMap也就是在所有已经加载的类中不包括beanName，则尝试从parentBeanFactory中检测
            if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
                String nameToLookup = originalBeanName(name);
                // 递归到BeanFactory中寻找
                if (args != null) {
                    return (T) parentBeanFactory.getBean(nameToLookup, args);
                } else {
                    return parentBeanFactory.getBean(nameToLookup, requiredType);
                }
            }

            // 如果不是仅仅做类型检查则是创建bean，这里要进行记录
            if (!typeCheckOnly) {
                markBeanAsCreated(beanName);
            }

            // 将存储XML配置文件的GernericBeanDefinition转换为RootBeanDefinition，如果指定BeanName是子Bean的话，会同时合并父类的相关属性
            final RootBeanDefinition mdb = getMergedLocalBeanDefinition(beanName);
            checkMergedBeanDefinition(mdb, beanName, args);

            String[] dependsOn = mdb.getDependsOn();
            // 若存在依赖则消需要递归实例化依赖的bean
            if (dependsOn != null) {
                for (String dependsOnBean : dependsOn) {
                    getBean(dependsOnBean);
                    // 缓存依赖调用
                    registerDependentBean(dependsOnBean, beanName);
                }
            }

            // 实例化依赖的bean后便可以实例化mdb本身了
            // singleton模式的创建
            if (mdb.isSingleton()) {
                sharedInstance =
                        getSingleton(
                                beanName,
                                new ObjectFactory<Object>() {
                                    public Object getObject() throws BeansException {
                                        try {
                                            return createBean(beanName, mdb, args);
                                        } catch (BeansException ex) {
                                            destorySingleton(beanName);
                                            throw ex;
                                        }
                                    }
                                });
                bean = getObjectForBeanInstance(sharedInstance, name, beanName, mdb);
            } else if (mdb.isPrototype()) {
                // prototype模式的创建（new）
                Object prototypeInstance = null;

                try {
                    beforePrototypeCreation(beanName);
                    prototypeInstance = createBean(beanName, mdb, args);
                } finally {
                    afterPrototypeCreation(beanName);
                }
                bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mdb);
            } else {
                // 指定的scope上实例化bean
                String scopeName = mdb.getScope();
                final Scope scope = this.scopes.get(scopeName);
                if (scope == null) {
                    throw new IllegalStateException(
                            "No Scope registered for scope'" + scopeName + "'");
                }
                try {
                    Object scopedInstance =
                            scope.get(
                                    beanName,
                                    new ObjectFactory<Object>() {
                                        public Object getObject() throws BeansException {
                                            try {
                                                return createBean(beanName, mdb, args);
                                            } finally {
                                                afterPrototypeCreation(beanName);
                                            }
                                        }
                                    });
                    bean = getObjectForBeanInstance(scopedInstance, name, beanName, mdb);
                } catch (IllegalStateException ex) {
                    throw new BeanCreationException(
                            beanName,
                            "Scope '"
                                    + scopeName
                                    + "' is not active for the current thread;"
                                    + "consider defining a scoped proxy for this bean if you in tend to refer to it from a singleton",
                            ex);
                }
            }
        }
        // 检查需要的类型是否符合bean的实际类型
        if (requiredType != null
                && bean != null
                && !requiredType.isAssignableFrom(bean.getClass())) {
            try {
                return getTypeConverter().convertIfNecessary(bean, requiredType);
            } catch (TypeMismatchException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug(
                            "Failed to convert bean '"
                                    + name
                                    + "' to required type ["
                                    + ClassUtils.getQualifiedName(requiredType)
                                    + "]",
                            ex);
                }
                throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
            }
        }
        return (T) bean;
    }

    public Object getSingleton(String beanName) {
        // 参数true设置标识允许早期依赖
        return getSingleton(beanName, true);
    }

    public Object getSingleton(String beanName, boolean allowEarlyReference) {
        // 检查缓存中是否存在实例
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            // 如果为空，则锁定全局变量并进行处理
            synchronized (this.singletonObjects) {
                // 如果此bean正在加载则不处理
                singletonObject = this.earlySingletonObjects.get(beanName);
                if (singletonObject == null && allowEarlyReference) {
                    // 当某些方法需要提前初始化的时候则会调用addSingletonFactory方法
                    // 将对应的ObjectFactory初始化策略存储在singletonFactories
                    ObjectFactory singletonFactory = this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        // 调用预先设定的getOject方法
                        singletonObject = singletonFactory.getObject();
                        // 记录在缓存中，earlySingletonObjects和singletonFactories互斥
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }
        // 书中代码为:return (singletonObject!= NULL_OBJECT?singletonObject:null); 最新代码如下，应该是为了精简代码
        return singletonObject;
    }

    protected Object getObjectForBeanInstance(
            Object beanInstance, String name, String beanName, RootBeanDefinition mbd) {

        // 如果指定的name是工厂相关（以&为前缀）且beanInstance又不是FactoryBean类型则验证不通过
        if (BeanFactoryUtils.isFactoryDereference(name) && !(beanInstance instanceof FactoryBean)) {
            throw new BeanIsNotAFactoryException(
                    transformedBeanName(name), beanInstance.getClass());
        }

        // 现在我们有了个bean的实例，这个实例可能会是正常的bean或者是FactoryBean
        // 如果是FactoryBean我们使用它创建实例，但是如果是用户想要直接获取工厂实例而不是工厂的getObject方法对应的实例
        // 那么传入的name应该加入前缀&
        if (!(beanInstance instanceof FactoryBean) || BeanFactoryUtils.isFactoryDereference(name)) {
            return beanInstance;
        }

        // 加载FactoryBean
        Object object = null;
        if (mbd == null) {
            // 尝试从缓存中加载bean
            object = getCachedObjectForFactoryBean(beanName);
        }
        if (object == null) {
            // 到这里已经明确知道beanInstance一定是FactoryBean类型
            FactoryBean<?> factoryBean = (FactoryBean<?>) beanInstance;
            // containsBeanDefinition检测beanDefinitionMap中也就是在所有已经加载的类中检测是否定义beanName
            if (mbd == null && containsBeanDefinition(beanName)) {
                // 将存储XML配置文件的GernericBeanDefinition转化为RootBeanDefinition,如果指定BeanName是子bean的话，同时会合并父类的相关属性
                mbd = getMergedLocalBeanDefinition(beanName);
            }

            // 是否是用户定义的而不是应用程序本身定义的
            boolean synthetic = (mbd != null && mbd.isSynthetic());
            object = getObjectFromFactoryBean(factoryBean, beanName, !synthetic);
        }
        return object;
    }

    protected Object getObjectFromFactoryBean(
            FactoryBean factoryBean, String beanName, Boolean shouldPostProess) {
        // 如果是单例模式
        if (factoryBean.isSingleton() && containsSingleton(beanName)) {
            // mutex 互斥体
            synchronized (getSingletonMutex()) {
                Object object = this.factoryBeanObjectCache.get(beanName);
                if (object == null) {
                    object = doGetObjectFromFactoryBean(factoryBean, beanName, shouldPostProess);
                    this.factoryBeanObjectCache.put(beanName, object);
                }
                return object;
            }
        } else {
            return doGetObjectFromFactoryBean(factoryBean, beanName, shouldPostProess);
        }
    }

    private Object doGetObjectFromFactoryBean(
            final FactoryBean factoryBean, final String beanName, final boolean shouldPostProcess)
            throws BeanCreationException {
        Object object;
        try {
            // 需要权限验证
            if (System.getSecurityManager() != null) {
                AccessControlContext acc = getAccessControlContext();
                try {
                    object =
                            AccessController.doPrivileged(
                                    new PrivilegedExceptionAction<Object>() {
                                        public Object run() throws Exception {
                                            return factoryBean.getObject();
                                        }
                                    },
                                    acc);
                } catch (PrivilegedActionException pae) {
                    throw pae.getException();
                }

            } else {
                // 直接调用getObject方法
                object = factoryBean.getObject();
            }

        } catch (FactoryBeanNotInitializedException ex) {
            throw new BeanCurrentlyInCreationException(beanName, ex.toString());
        } catch (Throwable ex) {
            throw new BeanCreationException(
                    beanName, "FactoryBean threw exception on object creation", ex);
        }

        if (object == null && isSingletonCurrentlyInCreation(beanName)) {
            throw new BeanCurrentlyInCreationException(
                    beanName,
                    "FactoryBean which is currently in creation returned null from getObject");
        }

        if (object != null && shouldPostProcess) {
            try {
                // 调用ObjectFactory的后处理器
                object = postProcessObjectFromFactoryBean(object, beanName);
            } catch (Throwable ex) {
                throw new BeanCreationException(
                        beanName, "POST-Processing of the Factory's object failed", ex);
            }
        }
        return object;
    }

    protected Object postProcessObjectFromFactoryBean(Object object, String beanName) {
        return applyBeanPostProcessorsAfterInitialization(object, beanName);
    }

    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
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

    public Object getSingleton(String beanName, ObjectFactory singletonFactory) {
        Assert.notNull(beanName, "'beanName' nust not be null");
        // 全局变量需要同步
        synchronized (this.singletonObjects) {
            // 首先检查对应的bean是否已经加载过，因为singleton模式其实就是复用已创建的bean，所以这一步是必须的
            Object singletonObject = this.singletonObjects.get(beanName);
            // 如果为空才可以进行singleton的bean的初始化
            if (singletonObject == null) {
                if (this.singletonsCurrentlyInDestruction) {
                    throw new BeanCreationNotAllowedException(
                            beanName,
                            "Singleton bean creation not allowed while singletons of this factory are in destruction "
                                    + "(Do not request a bean from a BeanFactory in a destroy method implementation!)");
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
                }
                beforeSingletonCreation(beanName);
                boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
                if (recordSuppressedExceptions) {
                    this.suppressedExceptions = new LinkedHashSet<Exception>();
                }

                try {
                    // 初始化bean
                    singletonObject = singletonFactory.getObject();
                } catch (BeanCreationException ex) {
                    if (recordSuppressedExceptions) {
                        for (Exception suppressedException : this.suppressedExceptions) {
                            ex.addRelatedCause(suppressedException);
                        }
                    }
                    throw ex;
                } finally {
                    if (recordSuppressedExceptions) {
                        this.suppressedExceptions = null;
                    }
                    afterSingletonCreation(beanName);
                }
                // 加入缓存
                addSingleton(beanName, singletonObject);
            }
            return singletonObject;
        }
    }

    protected void beforeSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName)
                && !this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new IllegalStateException(
                    "Singleton '" + beanName + "' isn't currectly in creation");
        }
    }

    protected void afterSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName)
                && !this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new IllegalStateException(
                    "Singleton'" + beanName + "' isn't currevtly in creation");
        }
    }

    protected void addSingleton(String beanName,Object singletonObject){
        synchronized (this.singletonObjects){
            this.singletonObjects.put(beanName,singletonObject);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }

}
*/
