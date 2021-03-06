package com.pushtorefresh.storio.sqlite.operations.get;

import android.database.Cursor;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Set;

import rx.Observable;
import rx.observers.TestSubscriber;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class PreparedGetObjectTest {

    public static class WithoutTypeMapping {

        @Test
        public void shouldGetByQueryWithoutTypeMappingBlocking() {
            final GetObjectStub getStub = GetObjectStub.newInstanceWithoutTypeMapping();

            final TestItem testItem = getStub.storIOSQLite
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyQueryBehavior(testItem);
        }

        @Test
        public void shouldGetObjectByQueryWithoutTypeMappingAsObservable() {
            final GetObjectStub getStub = GetObjectStub.newInstanceWithoutTypeMapping();

            final Observable<TestItem> testItemObservable = getStub.storIOSQLite
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .createObservable()
                    .take(1);

            getStub.verifyQueryBehavior(testItemObservable);
        }

        @Test
        public void shouldGetObjectByRawQueryWithoutTypeMappingBlocking() {
            final GetObjectStub getStub = GetObjectStub.newInstanceWithoutTypeMapping();

            final TestItem testItem = getStub.storIOSQLite
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.rawQuery)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyRawQueryBehavior(testItem);
        }

        @Test
        public void shouldGetObjectByRawQueryWithoutTypeMappingAsObservable() {
            final GetObjectStub getStub = GetObjectStub.newInstanceWithoutTypeMapping();

            final Observable<TestItem> testItemObservable = getStub.storIOSQLite
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.rawQuery)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .createObservable()
                    .take(1);

            getStub.verifyRawQueryBehavior(testItemObservable);
        }
    }

    public static class WithTypeMapping {

        @Test
        public void shouldGetObjectByQueryWithTypeMappingBlocking() {
            final GetObjectStub getStub = GetObjectStub.newInstanceWithTypeMapping();

            final TestItem testItem = getStub.storIOSQLite
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyQueryBehavior(testItem);
        }

        @Test
        public void shouldGetObjectByQueryWithTypeMappingAsObservable() {
            final GetObjectStub getStub = GetObjectStub.newInstanceWithTypeMapping();

            final Observable<TestItem> testItemObservable = getStub.storIOSQLite
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare()
                    .createObservable()
                    .take(1);

            getStub.verifyQueryBehavior(testItemObservable);
        }

        @Test
        public void shouldGetObjectByRawQueryWithTypeMappingBlocking() {
            final GetObjectStub getStub = GetObjectStub.newInstanceWithTypeMapping();

            final TestItem testItem = getStub.storIOSQLite
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.rawQuery)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyRawQueryBehavior(testItem);
        }

        @Test
        public void shouldGetObjectByRawQueryWithTypeMappingAsObservable() {
            final GetObjectStub getStub = GetObjectStub.newInstanceWithTypeMapping();

            final Observable<TestItem> testItemObservable = getStub.storIOSQLite
                    .get()
                    .object(TestItem.class)
                    .withQuery(getStub.rawQuery)
                    .prepare()
                    .createObservable()
                    .take(1);

            getStub.verifyRawQueryBehavior(testItemObservable);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithQueryBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.internal()).thenReturn(internal);

            final PreparedGet<TestItem> preparedGet = storIOSQLite
                    .get()
                    .object(TestItem.class)
                    .withQuery(Query.builder().table("test_table").build())
                    .prepare();

            try {
                preparedGet.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);
                assertThat(expected.getCause()).hasMessage("This type does not have type mapping: " +
                        "type = " + TestItem.class + "," +
                        "db was not touched by this operation, please add type mapping for this type");
            }

            verify(storIOSQLite).get();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).query(any(Query.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @SuppressWarnings("unchecked")
        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithQueryAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.internal()).thenReturn(internal);
            when(storIOSQLite.observeChangesInTables(any(Set.class)))
                    .thenReturn(Observable.empty());

            final TestSubscriber<TestItem> testSubscriber = new TestSubscriber<TestItem>();

            storIOSQLite
                    .get()
                    .object(TestItem.class)
                    .withQuery(Query.builder().table("test_table").build())
                    .prepare()
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class)
                    .hasMessageEndingWith("This type does not have type mapping: "
                            + "type = " + TestItem.class + "," +
                            "db was not touched by this operation, please add type mapping for this type");

            verify(storIOSQLite).get();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).query(any(Query.class));
            verify(storIOSQLite).observeChangesInTables(anySet());
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithRawQueryBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.internal()).thenReturn(internal);

            final PreparedGet<TestItem> preparedGet = storIOSQLite
                    .get()
                    .object(TestItem.class)
                    .withQuery(RawQuery.builder().query("test query").build())
                    .prepare();

            try {
                preparedGet.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);
                assertThat(expected.getCause()).hasMessage("This type does not have type mapping: " +
                        "type = " + TestItem.class + "," +
                        "db was not touched by this operation, please add type mapping for this type");
            }

            verify(storIOSQLite).get();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).rawQuery(any(RawQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithRawQueryAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.internal()).thenReturn(internal);

            final TestSubscriber<TestItem> testSubscriber = new TestSubscriber<TestItem>();

            storIOSQLite
                    .get()
                    .object(TestItem.class)
                    .withQuery(RawQuery.builder().query("test query").build())
                    .prepare()
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.getOnErrorEvents().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class)
                    .hasMessageEndingWith("This type does not have type mapping: "
                            + "type = " + TestItem.class + "," +
                            "db was not touched by this operation, please add type mapping for this type");

            verify(storIOSQLite).get();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).rawQuery(any(RawQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }
    }

    // Because we run tests on this class with Enclosed runner, we need to wrap other tests into class
    public static class OtherTests {

        @Test
        public void completeBuilderShouldThrowExceptionIfNoQueryWasSet() {
            PreparedGetObject.CompleteBuilder completeBuilder = new PreparedGetObject.Builder<Object>(mock(StorIOSQLite.class), Object.class)
                    .withQuery(Query.builder().table("test_table").build()); // We will null it later;

            completeBuilder.query = null;

            try {
                completeBuilder.prepare();
                failBecauseExceptionWasNotThrown(IllegalStateException.class);
            } catch (IllegalStateException expected) {
                assertThat(expected).hasMessage("Please specify Query or RawQuery");
                assertThat(expected).hasNoCause();
            }
        }

        @Test
        public void executeAsBlockingShouldThrowExceptionIfNoQueryWasSet() {
            //noinspection unchecked,ConstantConditions
            PreparedGetObject<Object> preparedGetObject
                    = new PreparedGetObject<Object>(
                    mock(StorIOSQLite.class),
                    Object.class,
                    (Query) null,
                    (GetResolver<Object>) mock(GetResolver.class)
            );

            try {
                preparedGetObject.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                IllegalStateException cause = (IllegalStateException) expected.getCause();
                assertThat(cause).hasMessage("Please specify query");
            }
        }

        @Test
        public void createObservableShouldThrowExceptionIfNoQueryWasSet() {
            //noinspection unchecked,ConstantConditions
            PreparedGetObject<Object> preparedGetOfObject
                    = new PreparedGetObject<Object>(
                    mock(StorIOSQLite.class),
                    Object.class,
                    (Query) null,
                    (GetResolver<Object>) mock(GetResolver.class)
            );

            try {
                //noinspection ResourceType
                preparedGetOfObject.createObservable();
                failBecauseExceptionWasNotThrown(IllegalStateException.class);
            } catch (IllegalStateException expected) {
                assertThat(expected)
                        .hasNoCause()
                        .hasMessage("Please specify query");
            }
        }

        @Test
        public void cursorMustBeClosedInCaseOfExceptionForExecuteAsBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);

            //noinspection unchecked
            final GetResolver<Object> getResolver = mock(GetResolver.class);

            final Cursor cursor = mock(Cursor.class);

            when(cursor.getCount()).thenReturn(10);

            when(cursor.moveToNext()).thenReturn(true);

            when(getResolver.performGet(eq(storIOSQLite), any(Query.class)))
                    .thenReturn(cursor);

            when(getResolver.mapFromCursor(cursor))
                    .thenThrow(new IllegalStateException("test exception"));

            PreparedGetObject<Object> preparedGetObject =
                    new PreparedGetObject<Object>(
                            storIOSQLite,
                            Object.class,
                            Query.builder().table("test_table").build(),
                            getResolver
                    );

            try {
                preparedGetObject.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException exception) {
                IllegalStateException cause = (IllegalStateException) exception.getCause();
                assertThat(cause).hasMessage("test exception");

                // Cursor must be closed in case of exception
                verify(cursor).close();

                verify(getResolver).performGet(eq(storIOSQLite), any(Query.class));
                verify(getResolver).mapFromCursor(cursor);
                verify(cursor).getCount();
                verify(cursor).moveToNext();

                verifyNoMoreInteractions(storIOSQLite, getResolver, cursor);
            }
        }

        @Test
        public void cursorMustBeClosedInCaseOfExceptionForObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);

            when(storIOSQLite.observeChangesInTables(eq(singleton("test_table"))))
                    .thenReturn(Observable.<Changes>empty());

            //noinspection unchecked
            final GetResolver<Object> getResolver = mock(GetResolver.class);

            final Cursor cursor = mock(Cursor.class);

            when(cursor.getCount()).thenReturn(10);

            when(cursor.moveToNext()).thenReturn(true);

            when(getResolver.performGet(eq(storIOSQLite), any(Query.class)))
                    .thenReturn(cursor);

            when(getResolver.mapFromCursor(cursor))
                    .thenThrow(new IllegalStateException("test exception"));

            PreparedGetObject<Object> preparedGetObject =
                    new PreparedGetObject<Object>(
                            storIOSQLite,
                            Object.class,
                            Query.builder().table("test_table").build(),
                            getResolver
                    );

            final TestSubscriber<Object> testSubscriber = new TestSubscriber<Object>();

            preparedGetObject
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();

            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            StorIOException storIOException = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

            IllegalStateException cause = (IllegalStateException) storIOException.getCause();
            assertThat(cause).hasMessage("test exception");

            // Cursor must be closed in case of exception
            verify(cursor).close();

            //noinspection unchecked
            verify(storIOSQLite).observeChangesInTables(anySet());
            verify(getResolver).performGet(eq(storIOSQLite), any(Query.class));
            verify(getResolver).mapFromCursor(cursor);
            verify(cursor).getCount();
            verify(cursor).moveToNext();

            verifyNoMoreInteractions(storIOSQLite, getResolver, cursor);
        }
    }
}
