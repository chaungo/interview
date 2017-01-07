package models;

import java.util.Map;
import java.util.TreeMap;

public class ComponentMetrics {
    private String m_name;
    private int m_id;
    private Map<String, String> m_metrics;

    public ComponentMetrics() {
        new ComponentMetrics("", 0, new TreeMap<String, String>());
    }

    public ComponentMetrics(String name, int id, Map<String, String> metrics) {
        m_name = name;
        m_id = id;
        m_metrics = metrics;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public int getId() {
        return m_id;
    }

    public void setId(int id) {
        m_id = id;
    }

    public Map<String, String> getMetrics() {
        return m_metrics;
    }

    public void setMetrics(Map<String, String> metrics) {
        m_metrics = metrics;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + m_id;
        result = prime * result + ((m_metrics == null) ? 0 : m_metrics.hashCode());
        result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
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
        ComponentMetrics other = (ComponentMetrics) obj;
        if (m_id != other.m_id)
            return false;
        if (m_metrics == null) {
            if (other.m_metrics != null)
                return false;
        } else if (!m_metrics.equals(other.m_metrics))
            return false;
        if (m_name == null) {
            if (other.m_name != null)
                return false;
        } else if (!m_name.equals(other.m_name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ComponentMetrics [m_name=" + m_name + ", m_id=" + m_id + ", m_metrics=" + m_metrics + "]";
    }


}
