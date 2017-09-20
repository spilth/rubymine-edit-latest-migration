package org.spilth.rubymine.migrations;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class EditLatestMigrationAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);

        if (project == null) return;

        File migrationsDirectory = getMigrationsDirectory(project);

        if (migrationsDirectory.exists() && migrationsDirectory.isDirectory()) {
            File[] migrationFiles = getMigrationFiles(migrationsDirectory);

            if (migrationFiles.length > 0) {
                File latestMigrationFile = migrationFiles[migrationFiles.length - 1];

                VirtualFile[] vFiles = ProjectRootManager.getInstance(project).getContentRoots();

                VirtualFile db = vFiles[0].findChild("db");
                VirtualFile migrate = db.findChild("migrate");
                VirtualFile latestMigrationVirtualFile = migrate.findChild(latestMigrationFile.getName());

                FileEditorManager.getInstance(project).openFile(latestMigrationVirtualFile, true);
            }
        } else {
            event.getPresentation().setEnabledAndVisible(false);
        }
    }

    @Override
    public void update(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);

        if (project == null) return;

        File migrationsDirectory = getMigrationsDirectory(project);

        if (migrationsDirectory.exists() && migrationsDirectory.isDirectory()) {
            File[] migrationFiles = getMigrationFiles(migrationsDirectory);

            event.getPresentation().setEnabledAndVisible(migrationFiles.length > 0);
        } else {
            event.getPresentation().setEnabledAndVisible(false);
        }
    }

    @NotNull
    private File getMigrationsDirectory(Project project) {
        return new File(project.getBasePath().concat(File.separator).concat("db").concat(File.separator).concat("migrate"));
    }

    private File[] getMigrationFiles(File migrationsDirectory) {
        return migrationsDirectory.listFiles((dir, name) -> name.endsWith(".rb"));
    }
}
