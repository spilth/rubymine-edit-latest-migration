package org.spilth.rubymine.migrations

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import java.io.File

class EditLatestMigrationAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(PlatformDataKeys.PROJECT) ?: return

        val migrationsDirectory = getMigrationsDirectory(project)

        if (migrationsDirectory.exists() && migrationsDirectory.isDirectory) {
            val migrationFiles = getMigrationFiles(migrationsDirectory)

            if (migrationFiles!!.isNotEmpty()) {
                val latestMigrationFile = migrationFiles[migrationFiles.size - 1]

                val vFiles = ProjectRootManager.getInstance(project).contentRoots

                val db = vFiles[0].findChild("db")
                val migrate = db!!.findChild("migrate")
                val latestMigrationVirtualFile = migrate!!.findChild(latestMigrationFile.name)

                FileEditorManager.getInstance(project).openFile(latestMigrationVirtualFile!!, true)
            }
        } else {
            event.presentation.isEnabledAndVisible = false
        }
    }

    override fun update(event: AnActionEvent?) {
        val project = event!!.getData(PlatformDataKeys.PROJECT) ?: return

        val migrationsDirectory = getMigrationsDirectory(project)

        if (migrationsDirectory.exists() && migrationsDirectory.isDirectory) {
            val migrationFiles = getMigrationFiles(migrationsDirectory)

            event.presentation.isEnabledAndVisible = migrationFiles!!.isNotEmpty()
        } else {
            event.presentation.isEnabledAndVisible = false
        }
    }

    private fun getMigrationsDirectory(project: Project): File {
        return File(project.basePath + File.separator + "db" + File.separator + "migrate")
    }

    private fun getMigrationFiles(migrationsDirectory: File): Array<File>? {
        return migrationsDirectory.listFiles { _, name -> name.endsWith(".rb") }
    }
}
