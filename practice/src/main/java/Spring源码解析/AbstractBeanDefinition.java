/*
package Spring源码解析;

import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.beans.factory.support.MethodOverride;
import org.springframework.beans.factory.support.MethodOverrides;
import org.springframework.util.ClassUtils;

public abstract class AbstractBeanDefinition extends BeanMetadataAttributeAccessor
        implements BeanDefinition, Cloneable {

    public void prepareMethodOverrides() throws BeanDefinitionValidationException {
        // check that lookup methods exists
        MethodOverrides methodOverrides = getMethodOverrides();
        if (methodOverrides.isEmpty()) {
            for (MethodOverride mo : methodOverrides.getOverrides()) {
                prepareMethodOverrides(mo);
            }
        }
    }

    protected void prepareMethodOverrides(MethodOverride mo)
            throws BeanDefinitionValidationException {
        // 获取对应类中对应方法名的个数
        int count = ClassUtils.getMethodCountForName(getBeanClass(), mo.getMethodName());
        if (count == 0) {
            throw new BeanDefinitionValidationException(
                    "Invalid method override: no method with name'"
                            + mo.getMethodName()
                            + "'on class ["
                            + getBeanClass()
                            + "]");
        } else if (count == 1) {
            // 标记MethodOverride暂未被覆盖，避免参数类型检查的开销
            mo.setOverloaded(false);
        }
    }

    @Override
    public void setParentName(String parentName) {}

    @Override
    public String getParentName() {
        return null;
    }

    @Override
    public void setBeanClassName(String beanClassName) {}

    @Override
    public String getBeanClassName() {
        return null;
    }

    @Override
    public void setScope(String scope) {}

    @Override
    public String getScope() {
        return null;
    }

    @Override
    public void setLazyInit(boolean lazyInit) {}

    @Override
    public boolean isLazyInit() {
        return false;
    }

    @Override
    public void setDependsOn(String... dependsOn) {}

    @Override
    public String[] getDependsOn() {
        return new String[0];
    }

    @Override
    public void setAutowireCandidate(boolean autowireCandidate) {}

    @Override
    public boolean isAutowireCandidate() {
        return false;
    }

    @Override
    public void setPrimary(boolean primary) {}

    @Override
    public boolean isPrimary() {
        return false;
    }

    @Override
    public void setFactoryBeanName(String factoryBeanName) {}

    @Override
    public String getFactoryBeanName() {
        return null;
    }

    @Override
    public void setFactoryMethodName(String factoryMethodName) {}

    @Override
    public String getFactoryMethodName() {
        return null;
    }

    @Override
    public ConstructorArgumentValues getConstructorArgumentValues() {
        return null;
    }

    @Override
    public MutablePropertyValues getPropertyValues() {
        return null;
    }

    @Override
    public void setInitMethodName(String initMethodName) {}

    @Override
    public String getInitMethodName() {
        return null;
    }

    @Override
    public void setDestroyMethodName(String destroyMethodName) {}

    @Override
    public String getDestroyMethodName() {
        return null;
    }

    @Override
    public void setRole(int role) {}

    @Override
    public int getRole() {
        return 0;
    }

    @Override
    public void setDescription(String description) {}

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public boolean isPrototype() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public String getResourceDescription() {
        return null;
    }

    @Override
    public BeanDefinition getOriginatingBeanDefinition() {
        return null;
    }

    @Override
    public Object getSource() {
        return null;
    }

    @Override
    public void setAttribute(String name, Object value) {}

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public Object removeAttribute(String name) {
        return null;
    }

    @Override
    public boolean hasAttribute(String name) {
        return false;
    }

    @Override
    public String[] attributeNames() {
        return new String[0];
    }
}
*/
