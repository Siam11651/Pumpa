module Pumpa
{
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    opens Pumpa to javafx.graphics, javafx.fxml;
}