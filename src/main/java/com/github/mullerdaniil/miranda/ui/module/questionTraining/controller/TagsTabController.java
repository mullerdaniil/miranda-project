package com.github.mullerdaniil.miranda.ui.module.questionTraining.controller;

import com.github.mullerdaniil.miranda.module.questionTraining.entity.Tag;
import com.github.mullerdaniil.miranda.module.questionTraining.exception.TagServiceException;
import com.github.mullerdaniil.miranda.module.questionTraining.service.TagService;
import com.github.mullerdaniil.miranda.ui.module.questionTraining.converter.ColorConverter;
import com.github.mullerdaniil.miranda.ui.module.questionTraining.event.TagsUpdatedEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.github.mullerdaniil.miranda.ui.util.DialogUtil.showErrorDialog;
import static com.github.mullerdaniil.miranda.ui.util.DialogUtil.showExceptionDialog;
import static javafx.collections.FXCollections.observableList;

@RequiredArgsConstructor
@Component
public class TagsTabController {
    @FXML
    private TextField tagNameTextField;
    @FXML
    private ColorPicker textColorColorPicker;
    @FXML
    private ColorPicker backgroundColorColorPicker;
    @FXML
    private ListView<Tag> tagsListView;

    private final TagService tagService;
    private final ColorConverter colorConverter;
    private final ApplicationEventPublisher eventPublisher;

    public void initialize() {
        tagsListView.getSelectionModel().selectedItemProperty()
                .addListener(new TagsListViewSelectionChangeListener());
        tagsListView.setCellFactory(new TagsListViewCellFactory());
        refreshTags();
    }

    public void createNewTag() {
        try {
            tagService.create(
                    tagNameTextField.getText(),
                    colorConverter.convertFrom(textColorColorPicker.getValue()),
                    colorConverter.convertFrom(backgroundColorColorPicker.getValue())
            );
            eventPublisher.publishEvent(new TagsUpdatedEvent());
        } catch (TagServiceException e) {
            showExceptionDialog(e);
        }
    }

    public void editSelectedTag() {
        var selectedTag = getSelectedTag();

        if (selectedTag != null) {
            try {
                tagService.update(
                        selectedTag.getId(),
                        tagNameTextField.getText(),
                        colorConverter.convertFrom(textColorColorPicker.getValue()),
                        colorConverter.convertFrom(backgroundColorColorPicker.getValue())
                );
                eventPublisher.publishEvent(new TagsUpdatedEvent());
            } catch (TagServiceException e) {
                showExceptionDialog(e);
            }
        } else {
            showErrorDialog("Tag is not selected.");
        }
    }

    @EventListener(TagsUpdatedEvent.class)
    public void refreshTags() {
        var previouslySelectedTag = getSelectedTag();
        tagsListView.setItems(observableList(tagService.findAll()));
        tagsListView.getSelectionModel().select(previouslySelectedTag);
    }

    private Tag getSelectedTag() {
        return tagsListView.getSelectionModel().getSelectedItem();
    }

    private void loadTag(Tag tag) {
        tagNameTextField.setText(tag.getName());
        textColorColorPicker.setValue(colorConverter.convertTo(tag.getTextColor()));
        backgroundColorColorPicker.setValue(colorConverter.convertTo(tag.getBackgroundColor()));
    }

    private class TagsListViewSelectionChangeListener implements ChangeListener<Tag> {
        @Override
        public void changed(ObservableValue<? extends Tag> observable, Tag oldValue, Tag newValue) {
            if (newValue != null) {
                loadTag(newValue);
            }
        }
    }

    private static class TagsListViewCellFactory implements Callback<ListView<Tag>, ListCell<Tag>> {
        @Override
        public ListCell<Tag> call(ListView<Tag> param) {
            return new ListCell<>() {
                @Override
                protected void updateItem(Tag tag, boolean empty) {
                    super.updateItem(tag, empty);

                    if (empty || tag == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle(null);
                    } else {
                        setText(tag.getName());
                    }
                }
            };
        }
    }
}