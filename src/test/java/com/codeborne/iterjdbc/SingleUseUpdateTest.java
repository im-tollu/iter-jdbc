package com.codeborne.iterjdbc;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SingleUseUpdateTest {
  ReusableUpdate reusableUpdate = mock(ReusableUpdate.class);
  SingleUseUpdate singleUseUpdate = new SingleUseUpdate(reusableUpdate);

  @Test
  void run() {
    Map<String, Object> params = new HashMap<>();
    int expectedAffectedRows = 234;
    when(reusableUpdate.run(any())).thenReturn(expectedAffectedRows);

    int actualAffectedRows = singleUseUpdate.run(params);

    verify(reusableUpdate).run(same(params));
    verify(reusableUpdate).close();
    assertThat(actualAffectedRows).isEqualTo(expectedAffectedRows);
  }

  @Test
  void runBatch() {
    Map<String, Object> params = singletonMap("param", "value");
    Iterator<Map<String, Object>> paramsIterator = singletonList(params).iterator();
    int expectedAffectedRows = 567;
    when(reusableUpdate.runBatch(any())).thenReturn(expectedAffectedRows);

    int actualAffectedRows = singleUseUpdate.runBatch(paramsIterator);

    verify(reusableUpdate).runBatch(same(paramsIterator));
    verify(reusableUpdate).close();
    assertThat(actualAffectedRows).isEqualTo(expectedAffectedRows);
  }
}
