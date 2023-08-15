package org.traccar.reports.aggregate;

public class Test {

    void doTest() {
        /*
        JPAQuery<Tuple> query = new JPAQuery<Tuple>(em);

        query.from(_city)
                .join(_city.state, _state)
                .join(_state.country, _country);

        List<Tuple> tupleResult = query.select(_city.id, _city.name,
                     _state.id, _state.name, _country.id, _country.name)
                .orderBy(_city.id.asc())
                .fetch();

        List<City> cities = QueryDslBinder.to(tupleResult, City.class,
                new GroupByBinder()
                        .key("id", _city.id)
                        .field("name", _city.name)
                        .single("state", new GroupByBinder()
                                .key("id", _state.id)
                                .field("name", _state.name)
                                .single("country", new GroupByBinder()
                                        .key("id", _country.id)
                                        .field("name", _country.name)
                                        .collection("states", new GroupByBinder()
                                                .key("id", _state.id)))));

         */
    }
}
