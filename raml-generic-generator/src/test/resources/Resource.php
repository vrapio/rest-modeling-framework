<?php
declare(strict_types = 1);
/**
 * This file has been auto generated
 * Do not change it
 */

namespace Test\Client;

use GuzzleHttp\Psr7;
use Psr\Http\Message\RequestInterface;

class Resource
{
    /**
     * @var string
     */
    private $uri;
    /**
     * @var array
     */
    private $args = [];

    /**
     * @param string $uri
     * @param array $args
     */
    public function __construct(string $uri, array $args = [])
    {
        $this->uri = $uri;
        $this->args = $args;
    }

    /**
     * @return string
     */
    final protected function getUri(): string
    {
        return $this->uri;
    }

    /**
     * @return array
     */
    final protected function getArgs(): array
    {
        return $this->args;
    }

    /**
     * @param string $method
     * @param string $uri
     * @param mixed $body
     * @param array $options
     * @param string $requestClass
     * @return ApiRequest
     */
    final protected function buildRequest(
        string $method,
        string $uri,
        $body = null,
        array $options = [],
        string $requestClass = ApiRequest::class
    ): ApiRequest {
        $headers = isset($options['headers']) ? $options['headers'] : [];
        /**
         * @var ApiRequest $request
         */
        $request = new $requestClass($method, $uri, $headers, $body);

        if (isset($options['query'])) {
            ksort($options['query']);
            $uri = $request->getUri()->withQuery(Psr7\build_query($options['query']));
            $request = $request->withUri($uri);
        }


        return $request;
    }
}
