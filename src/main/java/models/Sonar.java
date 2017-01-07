package models;

import java.util.Map;

public class Sonar {
    private String m_iaName;
    private Map<String, ComponentMetrics> m_componentMetrics;

    public Sonar() {
        super();
    }

    public Sonar(String iaName, Map<String, ComponentMetrics> componentMetrics) {
        super();
        m_iaName = iaName;
        m_componentMetrics = componentMetrics;
    }

    public String getIaName() {
        return m_iaName;
    }

    public void setIaName(String iaName) {
        m_iaName = iaName;
    }

    public Map<String, ComponentMetrics> getComponentMetrics() {
        return m_componentMetrics;
    }

    public void setComponentMetrics(Map<String, ComponentMetrics> componentMetrics) {
        m_componentMetrics = componentMetrics;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((m_componentMetrics == null) ? 0 : m_componentMetrics.hashCode());
        result = prime * result + ((m_iaName == null) ? 0 : m_iaName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Sonar other = (Sonar) obj;
        if (m_componentMetrics == null) {
            if (other.m_componentMetrics != null)
                return false;
        } else if (!m_componentMetrics.equals(other.m_componentMetrics))
            return false;
        if (m_iaName == null) {
            if (other.m_iaName != null)
                return false;
        } else if (!m_iaName.equals(other.m_iaName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Sonar [m_iaName=" + m_iaName + ", m_componentMetrics=" + m_componentMetrics + "]";
    }

}
