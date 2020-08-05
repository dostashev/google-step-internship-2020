// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    Event[] filteredEvents = events.stream()
      .filter(event ->
        attendeesIntersect(event.getAttendees(), request.getAttendees()) ||
        attendeesIntersect(event.getAttendees(), request.getOptionalAttendees())
      )
      .toArray(Event[]::new);

    Collection<TimeRange> result = getSuitableTimeRanges(filteredEvents, request);

    if (result.size() == 0) {
      filteredEvents = events.stream()
        .filter(event -> attendeesIntersect(event.getAttendees(), request.getAttendees()))
        .toArray(Event[]::new);

      result = getSuitableTimeRanges(filteredEvents, request);
    }

    return result;
  }

  private Collection<TimeRange> getSuitableTimeRanges(Event[] events, MeetingRequest request) {

    Event[] sortedEvents = events.clone();

    Arrays.sort(sortedEvents, new Comparator<Event>() {
      @Override
      public int compare(Event a, Event b) {
        return a.getWhen().start() == b.getWhen().start()
          ? TimeRange.ORDER_BY_END.compare(a.getWhen(), b.getWhen())
          : TimeRange.ORDER_BY_START.compare(a.getWhen(), b.getWhen());
      }
    });

    ArrayList<TimeRange> result = new ArrayList<>();

    if (sortedEvents.length == 0) {

      if (request.getDuration() <= TimeRange.WHOLE_DAY.duration())
        result.add(TimeRange.WHOLE_DAY);

    } else {

      int maxEnd = 0;

      for (int i = 0; i < sortedEvents.length; ++i) {
        if (sortedEvents[i].getWhen().start() - maxEnd >= request.getDuration())
          result.add(TimeRange.fromStartEnd(maxEnd, sortedEvents[i].getWhen().start(), false));

        maxEnd = Math.max(maxEnd, sortedEvents[i].getWhen().end());
      }

      if (TimeRange.END_OF_DAY + 1 - maxEnd >= request.getDuration())
        result.add(TimeRange.fromStartEnd(maxEnd, TimeRange.END_OF_DAY, true));

    }

    return result;
  }

  private boolean attendeesIntersect(Collection<String> a, Collection<String> b) {

    for (String attendee: a)
      if (b.contains(attendee))
        return true;

    return false;
  }
}
