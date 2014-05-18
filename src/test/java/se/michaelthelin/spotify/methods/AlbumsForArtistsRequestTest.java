package se.michaelthelin.spotify.methods;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import se.michaelthelin.spotify.Api;
import se.michaelthelin.spotify.TestConfiguration;
import se.michaelthelin.spotify.TestUtil;
import se.michaelthelin.spotify.models.AlbumType;
import se.michaelthelin.spotify.models.Page;
import se.michaelthelin.spotify.models.SimpleAlbum;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

@RunWith(MockitoJUnitRunner.class)
public class AlbumsForArtistsRequestTest {

  @Test
  public void shouldGetAlbumResultForArtistId_async() throws Exception {
    final Api api = Api.DEFAULT_API;

    final AlbumsForArtistRequest.Builder requestBuilder = api.getAlbumsForArtist("1vCWHaC5f2uS3yhpwWbIA6").limit(2).types(AlbumType.SINGLE);
    if (TestConfiguration.USE_MOCK_RESPONSES) {
      requestBuilder.httpManager(TestUtil.MockedHttpManager.returningJson("artist-album.json"));
    }
    final AlbumsForArtistRequest request = requestBuilder.build();

    final CountDownLatch asyncCompleted = new CountDownLatch(1);

    final SettableFuture<Page<SimpleAlbum>> albumsFuture = request.getAsync();

    Futures.addCallback(albumsFuture, new FutureCallback<Page<SimpleAlbum>>() {
      @Override
      public void onSuccess(Page<SimpleAlbum> albumSearchResult) {
        assertEquals("https://api.spotify.com/v1/artists/1vCWHaC5f2uS3yhpwWbIA6/albums?offset=0&limit=2&album_type=single", albumSearchResult.getHref());
        assertEquals(2, albumSearchResult.getLimit());
        assertEquals(0, albumSearchResult.getOffset());
        assertEquals(178, albumSearchResult.getTotal());
        assertEquals("https://api.spotify.com/v1/artists/1vCWHaC5f2uS3yhpwWbIA6/albums?offset=2&limit=2&album_type=single", albumSearchResult.getNext());
        assertEquals("null", albumSearchResult.getPrevious());

        List<SimpleAlbum> albums = albumSearchResult.getItems();
        assertEquals(2, albums.size());

        SimpleAlbum firstAlbum = albums.get(0);
        assertEquals(AlbumType.SINGLE, firstAlbum.getAlbumType());
        assertEquals("https://open.spotify.com/album/6HVPLh1TXzPnMqY7tAWLoL", firstAlbum.getExternalUrls().get("spotify"));
        assertEquals("https://api.spotify.com/v1/albums/6HVPLh1TXzPnMqY7tAWLoL", firstAlbum.getHref());
        assertEquals("6HVPLh1TXzPnMqY7tAWLoL", firstAlbum.getId());
        assertNotNull(firstAlbum.getImages());
        asyncCompleted.countDown();
      }

      @Override
      public void onFailure(Throwable throwable) {
        fail("Failed to resolve future");
      }
    });

    asyncCompleted.await(1, TimeUnit.SECONDS);
  }

  @Test
  public void shouldGetAlbumResultForArtistId_sync() throws Exception {
    final Api api = Api.DEFAULT_API;

    final AlbumsForArtistRequest.Builder requestBuilder = api.getAlbumsForArtist("1vCWHaC5f2uS3yhpwWbIA6").limit(2).types(AlbumType.SINGLE);
    if (TestConfiguration.USE_MOCK_RESPONSES) {
      requestBuilder.httpManager(TestUtil.MockedHttpManager.returningJson("artist-album.json"));
    }
    final AlbumsForArtistRequest request = requestBuilder.build();

    final Page<SimpleAlbum> albumSearchResult = request.get();

    assertEquals("https://api.spotify.com/v1/artists/1vCWHaC5f2uS3yhpwWbIA6/albums?offset=0&limit=2&album_type=single", albumSearchResult.getHref());
    assertEquals(2, albumSearchResult.getLimit());
    assertEquals(0, albumSearchResult.getOffset());
    assertEquals(178, albumSearchResult.getTotal());
    assertEquals("https://api.spotify.com/v1/artists/1vCWHaC5f2uS3yhpwWbIA6/albums?offset=2&limit=2&album_type=single", albumSearchResult.getNext());
    assertEquals("null", albumSearchResult.getPrevious());

    final List<SimpleAlbum> albums = albumSearchResult.getItems();
    assertEquals(2, albums.size());

    SimpleAlbum firstAlbum = albums.get(0);
    assertEquals(AlbumType.SINGLE, firstAlbum.getAlbumType());
    assertEquals("https://open.spotify.com/album/6HVPLh1TXzPnMqY7tAWLoL", firstAlbum.getExternalUrls().get("spotify"));
    assertEquals("https://api.spotify.com/v1/albums/6HVPLh1TXzPnMqY7tAWLoL", firstAlbum.getHref());
    assertEquals("6HVPLh1TXzPnMqY7tAWLoL", firstAlbum.getId());
    assertNotNull(firstAlbum.getImages());
  }

}