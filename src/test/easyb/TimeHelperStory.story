import com.moscona.util.TimeHelper

description "some of the functionality of the TimeHelper static library"

scenario "calculating a future time stamp from a HH:mm string", {
  given "a Calendar instance", {
    now = Calendar.instance
  }
  when "nextFutureTimeInLocalTime(16:03) is called", {
    result = TimeHelper.nextFutureTimeInLocalTime("16:03")
    cal = Calendar.instance
    cal.setTime(result)
  }
  then "the resulting time should be 16", {
    cal.get(Calendar.HOUR_OF_DAY).shouldBe 16
  }
  and "the resulting minute should be 3", {
    cal.get(Calendar.MINUTE).shouldBe 3
  }
  and "the resulting second should be 0", {
    cal.get(Calendar.SECOND).shouldBe 0
  }
  and "the result should be in the future", {
    cal.timeInMillis.shouldBeGreaterThan now.timeInMillis
  }
  and "the result should be no more than a day and one hour in the future", {
    (cal.timeInMillis - now.timeInMillis).shouldBeLessThan (25*3600000 as long)
  }
}

