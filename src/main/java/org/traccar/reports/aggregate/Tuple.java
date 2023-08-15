package org.traccar.reports.aggregate;

/**
 * {@code Tuple} defines an interface for generic query result projection
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * List<Tuple> result = query.from(employee).select(employee.firstName, employee.lastName).fetch();
 * for (Tuple row : result) {
 *     System.out.println("firstName " + row.get(employee.firstName));
 *     System.out.println("lastName " + row.get(employee.lastName));
 * }
 * }
 * </pre>
 *
 * @author tiwe
 *
 */
public interface Tuple {

    /**
     * Get a Tuple element by index
     *
     * @param <T> type of element
     * @param index zero based index
     * @param type type of element
     * @return element in array
     */

    <T> T get(int index, Class<T> type);

    /**
     * Get a tuple element by expression
     *
     * @param <T> type of element
     * @param expr expression key
     * @return result element that matches the expression
     */

    <T> T get(Expression<T> expr);

    /**
     * Get the size of the Tuple
     *
     * @return row element count
     */
    int size();

    /**
     * Get the content as an Object array
     *
     * @return tuple in array form
     */
    Object[] toArray();

    /**
     * All Tuples should override equals and hashCode. For compatibility
     * across different Tuple implementations, equality check should use
     * {@code java.util.Arrays#equals(Object[], Object[])} with {@code #toArray()} as parameters.
     */
    boolean equals(Object o);

    /**
     * All Tuples should override equals and hashCode. For compatibility
     * across different Tuple implementations, hashCode should use
     * {@code java.util.Arrays#hashCode(Object[])} with {@code #toArray()} as parameter.
     */
    int hashCode();


}
