package com.example.chatui.basic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

public class NoSelectionModel<T> extends MultipleSelectionModel<T> {
    @Override
    public void selectIndices(int index, int... indices) {
        // 不执行任何操作
    }

    @Override
    public void clearSelection(int index) {
        // 不执行任何操作
    }

    @Override
    public boolean isSelected(int index) {
        return false; // 始终返回未选中状态
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void selectPrevious() {

    }

    @Override
    public void selectNext() {

    }

    @Override
    public void selectAll() {
        // 不执行任何操作
    }

    @Override
    public void selectFirst() {

    }

    @Override
    public void selectLast() {

    }

    @Override
    public void clearSelection() {
        // 不执行任何操作
    }

    @Override
    public ObservableList<Integer> getSelectedIndices() {
        return FXCollections.observableArrayList(); // 返回空列表
    }

    @Override
    public ObservableList<T> getSelectedItems() {
        return FXCollections.observableArrayList(); // 返回空列表
    }

    @Override
    public void clearAndSelect(int i) {

    }

    @Override
    public void select(int i) {

    }

    @Override
    public void select(T obj) {
        // 不执行任何操作
    }
}
