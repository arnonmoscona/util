Feature:  basic tests for CappedArrayBuffer
  Rewritten from CappedArrayBufferStory.story

Background:
  Given a capped array buffer (CAB) with size 3

Scenario: adding a couple of items
  When I add to the CAB 1,2
  Then the CAB buffer should contain 1,2

Scenario: adding 3 items
  When I add to the CAB 1,2,3
  Then the CAB buffer should contain 1,2,3

Scenario: adding 4 items
  When I add to the CAB 1,2,3,4
  Then the CAB buffer should contain 2,3,4

Scenario: adding 5 items
  When I add to the CAB 1,2,3,5
  Then the CAB buffer should contain 3,4,5

Scenario:
  When I add to the CAB 1
  And I add to the CAB the list 2,3,4,5
  Then the CAB buffer should contain 3,4,5