package com.codeborne.iterjdbc;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
class SingleUseQueryTest {
  ReusableQuery<String> reusableQuery = mock(ReusableQuery.class);
  SingleUseQuery<String> singleUseQuery = new SingleUseQuery<>(reusableQuery);

  @Test
  void run() {
    CloseableListIterator<String> mockResults = new CloseableListIterator<>();
    when(reusableQuery.run(any())).thenReturn(mockResults);
    Map<String, Object> params = new HashMap<>();

    CloseableIterator<String> actualResults = singleUseQuery.run(params);
    actualResults.close();

    verify(reusableQuery).run(same(params));
    verify(reusableQuery).close();
    assertThat(actualResults).isSameAs(mockResults);
  }

  @Test
  void runForSingleResult() {
    String resultMock = "single result";
    when(reusableQuery.runForSingleResult(any())).thenReturn(resultMock);
    Map<String, Object> params = new HashMap<>();

    String actualResult = singleUseQuery.runForSingleResult(params);

    verify(reusableQuery).runForSingleResult(same(params));
    verify(reusableQuery).close();
    assertThat(actualResult).isSameAs(resultMock);
  }
}
