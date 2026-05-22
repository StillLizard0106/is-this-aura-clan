package com.gogrowglow.mapper;

import com.gogrowglow.dto.request.HabitRequest;
import com.gogrowglow.dto.request.TaskRequest;
import com.gogrowglow.dto.request.WellnessRequest;
import com.gogrowglow.dto.response.HabitResponse;
import com.gogrowglow.dto.response.TaskResponse;
import com.gogrowglow.dto.response.WellnessResponse;
import com.gogrowglow.entity.Habit;
import com.gogrowglow.entity.Task;
import com.gogrowglow.entity.WellnessEntry;

public class ProductivityMapper {

    public static Task toTask(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setNotes(request.getNotes());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setCompleted(false);
        return task;
    }

    public static TaskResponse toTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setNotes(task.getNotes());
        response.setDueDate(task.getDueDate());
        response.setPriority(task.getPriority());
        response.setCompleted(task.isCompleted());
        return response;
    }

    public static Habit toHabit(HabitRequest request) {
        Habit habit = new Habit();
        habit.setName(request.getName());
        habit.setCadence(request.getCadence());
        habit.setDescription(request.getDescription());
        habit.setStreak(0);
        return habit;
    }

    public static HabitResponse toHabitResponse(Habit habit) {
        HabitResponse response = new HabitResponse();
        response.setId(habit.getId());
        response.setName(habit.getName());
        response.setCadence(habit.getCadence());
        response.setDescription(habit.getDescription());
        response.setStreak(habit.getStreak());
        response.setCompletedToday(false);
        return response;
    }

    public static WellnessEntry toWellnessEntry(WellnessRequest request) {
        WellnessEntry entry = new WellnessEntry();
        entry.setDate(request.getDate());
        entry.setMoodScore(request.getMoodScore());
        entry.setEnergyScore(request.getEnergyScore());
        entry.setStressScore(request.getStressScore());
        entry.setNotes(request.getNotes());
        return entry;
    }

    public static WellnessResponse toWellnessResponse(WellnessEntry entry) {
        WellnessResponse response = new WellnessResponse();
        response.setId(entry.getId());
        response.setDate(entry.getDate());
        response.setMoodScore(entry.getMoodScore());
        response.setEnergyScore(entry.getEnergyScore());
        response.setStressScore(entry.getStressScore());
        response.setNotes(entry.getNotes());
        return response;
    }
}
