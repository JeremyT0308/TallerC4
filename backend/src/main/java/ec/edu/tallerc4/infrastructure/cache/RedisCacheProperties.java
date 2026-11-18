package ec.edu.tallerc4.infrastructure.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redis")
public class RedisCacheProperties {
    private boolean enabled = false;
    private int freshMinutes = 15;
    private int maxStaleDays = 7;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getFreshMinutes() { return freshMinutes; }
    public void setFreshMinutes(int freshMinutes) { this.freshMinutes = freshMinutes; }
    public int getMaxStaleDays() { return maxStaleDays; }
    public void setMaxStaleDays(int maxStaleDays) { this.maxStaleDays = maxStaleDays; }
}
