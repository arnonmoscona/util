import com.moscona.util.windows.TaskList

description "task list functionality in Windows"

before_each "scenario", {
  given "a TaskList instance", {
    taskList = new TaskList()
  }
  and "a result placeholder", {
    result = null
  }
}

scenario "listing processes", {
  when "I call listTasks()", {
    result = taskList.listTasks()
  }
  then "I should get a list of some length", {
    result.size().shouldBeGreaterThan(10)
  }
}

scenario "getting details of a specific process name", {
  when "I call listTasks(\"winlogon.exe\")", {
    result = taskList.listTasks("winlogon.exe")
  }
  then "I should get a list of some length", {
    result.size().shouldBeGreaterThan(0)
  }
  and "its should match the name", {
    result[0].name.shouldBe("winlogon.exe")
  }
  and "I should be able to get a PID", {
    result[0].pid.shouldBeGreaterThan(0)
  }
}