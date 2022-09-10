package com.company.view.resourses.verticalLayout;

import java.awt.*;

    public class VerticalLayout implements LayoutManager
    {
        private final Dimension size = new Dimension();

        public void addLayoutComponent (String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension minimumLayoutSize(Container c) {
            return size;
        }

        public Dimension preferredLayoutSize(Container c) {
            return size;
        }

        public void layoutContainer(Container container)
        {
            Component[] list = container.getComponents();
            int currentY = 50;
            for(int i = 0; i < list.length; i++) {
                Component component = list[i];
                if(i == 3 || i == 4) {
                    component.setPreferredSize(new Dimension(400,40));
                    Dimension pref = component.getPreferredSize();
                    component.setBounds(200, currentY, pref.width, pref.height);
                    if(i == 4) {
                        currentY += 60;
                    }
                    else {
                        currentY += 40;

                    }
                    currentY += pref.height;
                }
                else {
                    component.setPreferredSize(new Dimension(400, 80));
                    Dimension pref = component.getPreferredSize();
                    component.setBounds(200, currentY, pref.width, pref.height);
                    currentY += 60;
                    currentY += pref.height;
                }
            }
        }
    }
