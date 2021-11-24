package edu.tec.ic6821.blog.posts.model.hsqldb;

import edu.tec.ic6821.blog.posts.model.Post;
import edu.tec.ic6821.blog.posts.model.PostDao;
import edu.tec.ic6821.blog.users.model.User;
import edu.tec.ic6821.blog.users.model.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest
public class HSQLDBPostDaoIntegrationTest {

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserDao userDao;

    @Test
    public void create() {
        // given
        final LocalDateTime momentBeforeCall = LocalDateTime.now();

        final User user = new User(
            101L,
            "username|" + UUID.randomUUID(),
            "name1",
            "email1",
            "zipcode1"
        );
        final User savedUser = userDao.create(user);

        final Post post = new Post(150L, 101L, savedUser.getId(), "title1", "body1");

        // when
        final Post actual = postDao.create(post);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getIntegrationId()).isEqualTo(post.getIntegrationId());
        assertThat(actual.getUserIntegrationId()).isEqualTo(post.getUserIntegrationId());
        assertThat(actual.getUserId()).isEqualTo(post.getUserId());
        assertThat(actual.getTitle()).isEqualTo(post.getTitle());
        assertThat(actual.getBody()).isEqualTo(post.getBody());
        assertThat(actual.getExtId()).isNotNull();
        assertThat(UUID.fromString(actual.getExtId())).isNotNull();
        assertThat(actual.getCreatedOn()).isAfter(momentBeforeCall);
        assertThat(actual.getLastUpdatedOn()).isAfter(momentBeforeCall);
    }

    @Test
    public void batchCreate() {
        // given
        final User user = new User(
            101L,
            "username|" + UUID.randomUUID(),
            "name1",
            "email1",
            "zipcode1"
        );
        final User savedUser = userDao.create(user);

        final List<Post> posts = List.of(
            new Post(150L, 101L, savedUser.getId(), "title1", "body1"),
            new Post(151L, 101L, savedUser.getId(), "title2", "body2"),
            new Post(152L, 101L, savedUser.getId(), "title3", "body3")
        );

        // when
        int count = postDao.create(posts);

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    public void findById() {
        // given
        final User user = new User(
            101L,
            "username|" + UUID.randomUUID(),
            "name1",
            "email1",
            "zipcode1"
        );
        final User savedUser = userDao.create(user);
        final Post post = new Post(150L, 101L, savedUser.getId(), "title1", "body1");
        final Post savedPost = postDao.create(post);

        // when
        Optional<Post> actual = postDao.findById(savedPost.getId());

        // then
        assertThat(actual).isNotEmpty();
        final Post retrievedPost = actual.get();
        assertThat(retrievedPost.getId()).isEqualTo(savedPost.getId());
        assertThat(retrievedPost.getIntegrationId()).isEqualTo(savedPost.getIntegrationId());
        assertThat(retrievedPost.getUserIntegrationId()).isEqualTo(savedPost.getUserIntegrationId());
        assertThat(retrievedPost.getUserId()).isEqualTo(savedPost.getUserId());
        assertThat(retrievedPost.getTitle()).isEqualTo(savedPost.getTitle());
        assertThat(retrievedPost.getBody()).isEqualTo(savedPost.getBody());
        assertThat(retrievedPost.getExtId()).isEqualTo(savedPost.getExtId());
        assertThat(retrievedPost.getCreatedOn()).isCloseTo(savedPost.getCreatedOn(), within(1, ChronoUnit.SECONDS));
        assertThat(retrievedPost.getLastUpdatedOn()).isCloseTo(savedPost.getLastUpdatedOn(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    public void findByIdNoResult() {
        // given
        Long nonExistingId = -1L;

        // when
        final Optional<Post> actual = postDao.findById(nonExistingId);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    public void findByExtId() {
        // given
        final User user = new User(
            101L,
            "username|" + UUID.randomUUID(),
            "name1",
            "email1",
            "zipcode1"
        );
        final User savedUser = userDao.create(user);
        final Post post = new Post(150L, 101L, savedUser.getId(), "title1", "body1");
        final Post savedPost = postDao.create(post);

        // when
        Optional<Post> actual = postDao.findByExtId(savedPost.getExtId());

        // then
        assertThat(actual).isNotEmpty();
        final Post retrievedPost = actual.get();
        assertThat(retrievedPost.getId()).isEqualTo(savedPost.getId());
        assertThat(retrievedPost.getIntegrationId()).isEqualTo(savedPost.getIntegrationId());
        assertThat(retrievedPost.getUserIntegrationId()).isEqualTo(savedPost.getUserIntegrationId());
        assertThat(retrievedPost.getUserId()).isEqualTo(savedPost.getUserId());
        assertThat(retrievedPost.getTitle()).isEqualTo(savedPost.getTitle());
        assertThat(retrievedPost.getBody()).isEqualTo(savedPost.getBody());
        assertThat(retrievedPost.getExtId()).isEqualTo(savedPost.getExtId());
        assertThat(retrievedPost.getCreatedOn()).isCloseTo(savedPost.getCreatedOn(), within(1, ChronoUnit.SECONDS));
        assertThat(retrievedPost.getLastUpdatedOn()).isCloseTo(savedPost.getLastUpdatedOn(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    public void findByExtIdNoResult() {
        // given
        final String nonExistingId = UUID.randomUUID().toString();

        // when
        final Optional<Post> actual = postDao.findByExtId(nonExistingId);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    public void getAll() {
        // given
        final User user = new User(
            101L,
            "username|" + UUID.randomUUID(),
            "name1",
            "email1",
            "zipcode1"
        );
        final User savedUser = userDao.create(user);

        final List<Post> posts = List.of(
            new Post(150L, 101L, savedUser.getId(), "title1", "body1"),
            new Post(151L, 101L, savedUser.getId(), "title2", "body2"),
            new Post(152L, 101L, savedUser.getId(), "title3", "body3")
        );
        int count = postDao.create(posts);

        // when
        final List<Post> actual = postDao.getAll();

        // then
        assertThat(actual).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    public void findByUserId() {
        // given
        final User user = new User(
            101L,
            "username|" + UUID.randomUUID(),
            "name1",
            "email1",
            "zipcode1"
        );
        final User savedUser = userDao.create(user);

        final Post savedPost1 = postDao.create(new Post(150L, 101L, savedUser.getId(), "title1", "body1"));
        final Post savedPost2 = postDao.create(new Post(151L, 101L, savedUser.getId(), "title2", "body2"));
        final Post savedPost3 = postDao.create(new Post(152L, 101L, savedUser.getId(), "title3", "body3"));

        // when
        final List<Post> actual = postDao.findByUserId(savedUser.getId());

        // then
        assertThat(actual).containsAll(List.of(savedPost1, savedPost2, savedPost3));
    }

    @Test
    public void findByUserIdNoResults() {
        // given
        Long nonExistingId = -1L;

        // when
        final List<Post> actual = postDao.findByUserId(nonExistingId);

        // then
        assertThat(actual).isEmpty();
    }
}
