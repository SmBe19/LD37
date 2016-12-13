package com.smeanox.games.ld37.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.smeanox.games.ld37.Consts;
import com.smeanox.games.ld37.LD37;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Consts.WIDTH, Consts.HEIGHT);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new LD37();
        }
}