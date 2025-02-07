package com.example.noticias.controladores;

import com.example.noticias.modelos.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import java.sql.*;

public class noticiasController {

    @FXML
    private Button addFavButton;

    @FXML
    private Label labelAutor;

    @FXML
    private Label labelFecha;

    @FXML
    private Label labelTema;

    @FXML
    private Label labelTitulo;

    @FXML
    private ListView<String> listViewNoticias;

    public void obtener() {
        ObservableList<String> listaTitulos = FXCollections.observableArrayList();
        Conexion conexion = new Conexion();
        String sql = "SELECT titulo FROM noticias";

        try (Connection con = conexion.getConexion();
             Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {

            listaTitulos.clear();

            while (rs.next()) {
                String titulo = rs.getString("titulo");
                listaTitulos.add(titulo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        listViewNoticias.setItems(listaTitulos);
    }

    @FXML
    void showNoticias(MouseEvent event) {
        String tituloSeleccionado = listViewNoticias.getSelectionModel().getSelectedItem();
        Conexion conexion = new Conexion();
        String sql = "SELECT * FROM noticias WHERE titulo = '" + tituloSeleccionado + "'";

        try (Statement stm = conexion.getConexion().createStatement();
             ResultSet rs = stm.executeQuery(sql)) {

            while (rs.next()){
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                String fecha = rs.getString("fecha");
                String tema = rs.getString("tema");
                String contenido = rs.getString("contenido");

                labelTitulo.setText(titulo);
                labelAutor.setText(autor);
                labelFecha.setText(fecha);
                labelTema.setText(tema);
                textAreaContenido.setText(contenido);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private Button showFavButton;

    @FXML
    private TextArea textAreaContenido;
    @FXML
    private Button delFav;

    @FXML
    void showFav(MouseEvent event) {
        ObservableList<String> listaTitulos = FXCollections.observableArrayList();
        Conexion conexion = new Conexion();
        String sql;

        if (showFavButton.getText().equals("Mostrar favoritos")) {
            sql = "SELECT titulo FROM noticias WHERE favorito = 1";
            showFavButton.setText("Mostrar Todos");
        } else {
            sql = "SELECT titulo FROM noticias";
            showFavButton.setText("Mostrar favoritos");
        }

        try (Statement st = conexion.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            listaTitulos.clear();

            while (rs.next()) {
                String titulo = rs.getString("titulo");
                listaTitulos.add(titulo);
            }

            if (listaTitulos.isEmpty()) {
                listaTitulos.add("No hay favoritos para mostrar.");
            }

            listViewNoticias.setItems(listaTitulos);

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener los títulos: ", e);
        }
    }

    @FXML
    void AddFav(MouseEvent event) {
        String tituloSeleccionado = listViewNoticias.getSelectionModel().getSelectedItem();

        if (tituloSeleccionado != null) {
            Conexion conexion = new Conexion();
            String sql = "UPDATE noticias SET favorito = 1 WHERE titulo = ?";

            try (PreparedStatement stm = conexion.getConexion().prepareStatement(sql)) {
                stm.setString(1, tituloSeleccionado);
                int rowsAffected = stm.executeUpdate();

                if (rowsAffected > 0) {
                    addFavButton.setDisable(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                conexion.close();
            }
        }
    }

    private void leerFav(String tituloSeleccionado) {
        Conexion conexion = new Conexion();
        String sql = "SELECT favorito FROM noticias WHERE titulo = ?";

        try (PreparedStatement stm = conexion.getConexion().prepareStatement(sql)) {
            stm.setString(1, tituloSeleccionado);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                int favorito = rs.getInt("favorito");
                if (favorito == 1) {
                    addFavButton.setDisable(true); // Deshabilitar si es un favorito.
                } else {
                    addFavButton.setDisable(false); // Habilitar si no es un favorito.
                }
            } else {
                addFavButton.setDisable(false); // Asegurarse de habilitarlo si no se encuentra.
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conexion.close();
        }
    }

    @FXML
    void delFavAction(MouseEvent event) {
        ObservableList<String> listaTitulos = FXCollections.observableArrayList();
        Conexion conexion = new Conexion();
        String sql = "UPDATE noticias SET favorito = 0 WHERE favorito = 1";

        try (PreparedStatement stm = conexion.getConexion().prepareStatement(sql)) {
            int rowsAffected = stm.executeUpdate();

            if (rowsAffected > 0) {
            }

            showFavButton.setText("Mostrar Favoritos");
            showFavButton.setDisable(false);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        obtener();
        addFavButton.setDisable(false);
    }

    @FXML
    public void initialize() {
        obtener();

        listViewNoticias.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                leerFav(newValue); // Verificar si el título seleccionado es favorito y deshabilitar el botón.
            } else {
                addFavButton.setDisable(true); // Deshabilitar el botón si no hay título seleccionado.
            }
        });

        addFavButton.setDisable(true); // Deshabilitar el botón al inicio.
    }
}