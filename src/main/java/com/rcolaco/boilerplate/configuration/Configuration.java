package com.rcolaco.boilerplate.configuration;

/**
 *
 */
public class Configuration
{
    private String text = "foo";
    private int numeric = 27;
    private NestedConfiguration inner;

    public NestedConfiguration getInner()
    {
        return inner;
    }

    public void setInner(NestedConfiguration inner)
    {
        this.inner = inner;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public int getNumeric()
    {
        return numeric;
    }

    public void setNumeric(int numeric)
    {
        this.numeric = numeric;
    }

    public static class NestedConfiguration
    {
        private boolean enabled = false;

        public boolean isEnabled()
        {
            return enabled;
        }

        public void setEnabled(boolean enabled)
        {
            this.enabled = enabled;
        }

        @Override
        public String toString()
        {
            return "NestedConfiguration{" +
                "enabled=" + enabled +
                '}';
        }
    }

    @Override
    public String toString()
    {
        return "Configuration{" +
            "text='" + text + '\'' +
            ", numeric=" + numeric +
            ", inner=" + inner +
            '}';
    }
}
