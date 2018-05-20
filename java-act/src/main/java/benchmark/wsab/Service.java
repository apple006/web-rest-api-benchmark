package benchmark.wsab;

import act.Act;
import act.db.jpa.JPADao;
import act.job.OnAppStart;
import act.util.JsonView;
import benchmark.wsab.model.Country;
import benchmark.wsab.model.User;
import com.alibaba.fastjson.JSON;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.util.C;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javax.persistence.Query;

@SuppressWarnings("unused")
@JsonView
public class Service {

    @OnAppStart
    public void bind() {
        Act.getNonblock("/hello", context ->
            context.resp().writeContent(JSON.toJSONString(C.Map("hello", "world")))
        );
        Act.getNonblock("/compute", context -> {
            long x = 0, y = 1, z, max;

            Random r = ThreadLocalRandom.current();
            max = 10000 + r.nextInt(500);

            for (int i = 0; i <= max; i++) {
                z = x + y;
                x = y;
                y = z;
            }

            context.resp().writeContent(JSON.toJSONString(C.Map("status", "done")));
        });
    }

    @GetAction("/countries")
    public List<Country> countries(JPADao<Integer, Country> dao) {
        return dao.q().fetch();
    }

    @GetAction("/users")
    public List<User> users2(JPADao<Integer, User> dao) {
        Query q = dao.em().createQuery("select u from User u join u.countries c where c.name = :countryName");
        q.setParameter("countryName", "France");
        return q.getResultList();
    }

}
