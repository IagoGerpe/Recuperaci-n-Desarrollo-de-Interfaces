package com.recuperacion.carmanager;

import com.recuperacion.carmanager.model.User;
import com.recuperacion.carmanager.utils.RememberSessionUtil;
import com.recuperacion.carmanager.utils.Session;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        AppShell.initialize(stage);

        User rememberedUser = RememberSessionUtil.loadRememberedUser();

        if (rememberedUser != null) {
            Session.setCurrentUser(rememberedUser);
            AppShell.showMainView();
        } else {
            AppShell.showLoginView();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}